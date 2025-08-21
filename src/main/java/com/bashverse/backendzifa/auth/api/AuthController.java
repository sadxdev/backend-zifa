package com.bashverse.backendzifa.auth.api;

import com.bashverse.backendzifa.auth.domain.LogoutRequest;
import com.bashverse.backendzifa.auth.domain.RefreshTokenRequest;
import com.bashverse.backendzifa.auth.infra.keycloak.KeycloakAdminClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakAdminClient keycloakAdminClient;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        keycloakAdminClient.logout(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody RefreshTokenRequest request) {
        Map<String, Object> tokens = keycloakAdminClient.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(tokens);
    }
}
