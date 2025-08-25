package com.bashverse.backendzifa.auth.api;

import com.bashverse.backendzifa.auth.domain.ForgotPasswordRequest;
import com.bashverse.backendzifa.auth.domain.LogoutRequest;
import com.bashverse.backendzifa.auth.domain.RefreshTokenRequest;
import com.bashverse.backendzifa.auth.domain.ResetPasswordRequest;
import com.bashverse.backendzifa.auth.infra.keycloak.KeycloakAdminClient;
import com.bashverse.backendzifa.auth.service.ForgotPasswordService;
import com.bashverse.backendzifa.auth.service.ResetPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakAdminClient keycloakAdminClient;

    private final ForgotPasswordService forgotPasswordService;
    private final ResetPasswordService resetPasswordService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            forgotPasswordService.initiatePasswordReset(request.getEmail());
        } catch (Exception e) {
            logger.error("Error initiating password reset for email: {}", request.getEmail(), e);
            // Do not reveal error details to caller to avoid user enumeration
        }
        // Always return 200 OK to avoid exposing user existence
        return ResponseEntity.ok(Map.of("message", "If the email exists, password reset instructions have been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            resetPasswordService.resetPassword(request.getResetToken(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password has been reset successfully."));
        } catch (Exception e) {
            logger.error("Error resetting password for token: {}", request.getResetToken(), e);
            // Return a generic error message to avoid revealing details
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token or password does not meet requirements."));

        }
    }
}