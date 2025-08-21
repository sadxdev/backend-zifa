package com.bashverse.backendzifa.auth.api;

import com.bashverse.backendzifa.auth.domain.LogoutRequest;
import com.bashverse.backendzifa.auth.infra.keycloak.KeycloakAdminClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
