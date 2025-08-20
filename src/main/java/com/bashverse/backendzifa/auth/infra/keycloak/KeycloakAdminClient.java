package com.bashverse.backendzifa.auth.infra.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
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
}
