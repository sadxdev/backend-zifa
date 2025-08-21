package com.bashverse.backendzifa.auth.infra.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeycloakAdminClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}") // client_id
    private String clientId;

    @Value("${keycloak.credentials.secret}") // client_secret
    private String clientSecret;

    /**
     * Create a new user in Keycloak
     * @return userId (UUID) from Keycloak
     */
    public String createUser(String username, String email, String password) {
        String token = getAdminAccessToken();

        Map<String, Object> userPayload = Map.of(
                "username", username,
                "email", email,
                "enabled", true,
                "credentials", new Object[]{
                        Map.of(
                                "type", "password",
                                "value", password,
                                "temporary", false
                        )
                }
        );

        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userPayload, headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
            if (location != null) {
                return location.substring(location.lastIndexOf("/") + 1);
            } else {
                throw new RuntimeException("User created but location header not found");
            }
        } else {
            throw new RuntimeException("Failed to create user: " + response.getStatusCode());
        }
    }

    /**
     * Get admin access token from Keycloak using client_credentials
     */
    private String getAdminAccessToken() {
        String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(URI.create(tokenUrl), entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map body = response.getBody();
            if (body != null && body.containsKey("access_token")) {
                return (String) body.get("access_token");
            } else {
                throw new RuntimeException("Token response missing access_token");
            }
        } else {
            throw new RuntimeException("Failed to get admin access token: " + response.getStatusCode());
        }
    }

    /**
     * Logout user by revoking refresh token in Keycloak
     */
    public void logout(String refreshToken) {
        String url = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Logout from Keycloak failed with status: " + response.getStatusCode());
        }
    }
    /**
     * Refresh access token using Keycloak refresh token endpoint.
     * Accepts a refresh token and returns a new access token (and optionally a new refresh token).
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(URI.create(tokenUrl), entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map body = response.getBody();
            if (body != null && body.containsKey("access_token")) {
                return body;
            } else {
                throw new RuntimeException("Token refresh response missing access_token");
            }
        } else {
            throw new RuntimeException("Failed to refresh token: " + response.getStatusCode());
        }
    }

    /**
     * Find user by email using Keycloak Admin API
     * Returns list of users matching email, usually only one expected
     */
    public List<Map<String, Object>> findUserByEmail(String email) {
        String token = getAdminAccessToken();

        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users?email=" + email;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to search user by email: " + response.getStatusCode());
        }
    }

    /**
     * Trigger password reset by setting UPDATE_PASSWORD required action on the user
     */
    public void triggerPasswordReset(String userId) {
        String token = getAdminAccessToken();

        String url = keycloakServerUrl + "/admin/realms/" + realm + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Required action for password reset
        Map<String, Object> body = Map.of(
                "enabled", true,  // keep user enabled
                "requiredActions", List.of("UPDATE_PASSWORD")
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to trigger password reset: " + response.getStatusCode());
        }
    }
}

