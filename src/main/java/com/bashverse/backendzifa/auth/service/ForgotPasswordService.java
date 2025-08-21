package com.bashverse.backendzifa.auth.service;

import com.bashverse.backendzifa.auth.infra.keycloak.KeycloakAdminClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service to handle forgot-password functionality by interacting with Keycloak.
 */
@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final KeycloakAdminClient keycloakAdminClient;
    private final Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class);

    /**
     * Initiates password reset by sending reset email via Keycloak for the specified email.
     * Does not reveal whether user exists to prevent user enumeration.
     *
     * @param email the user's email address
     */
    public void initiatePasswordReset(String email) {
        try {
            List<Map<String, Object>> users = keycloakAdminClient.findUserByEmail(email);
            if (users != null && !users.isEmpty()) {
                String userId = (String) users.get(0).get("id");
                keycloakAdminClient.triggerPasswordReset(userId);
                logger.info("Password reset triggered for userId: {}", userId);
            } else {
                // No user found: log info but do not disclose to caller
                logger.info("Password reset requested for non-existing email: {}", email);
            }
        } catch (Exception e) {
            // Log exception but don't propagate to avoid info disclosure
            logger.error("Error initiating password reset for email: {}", email, e);
        }
    }
}
