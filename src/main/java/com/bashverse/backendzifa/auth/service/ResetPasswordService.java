package com.bashverse.backendzifa.auth.service;

import com.bashverse.backendzifa.auth.infra.keycloak.KeycloakAdminClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service to handle password reset by validating reset tokens and updating user password in Keycloak.
 */
@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final KeycloakAdminClient keycloakAdminClient;

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordService.class);

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 64;

    /**
     * Resets the user's password after validating the reset token.
     *
     * @param resetToken the password reset token provided by the user
     * @param newPassword the new password to set
     */
    public void resetPassword(String resetToken, String newPassword) {
        validateNewPassword(newPassword);

        // Validate the reset token (this method should verify token validity, expiration, and extract userId)
        String userId = validateResetTokenAndGetUserId(resetToken);

        // Update the password using Keycloak Admin API
        keycloakAdminClient.updateUserPassword(userId, newPassword);

        logger.info("Password reset successful for userId: {}", userId);
    }

    /**
     * Validates password according to enterprise password policies.
     *
     * @param password the password to validate
     */
    private void validateNewPassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be between " + MIN_PASSWORD_LENGTH + " and " + MAX_PASSWORD_LENGTH + " characters");
        }
        // Add other password policy validations here (e.g., complexity, breach check)
    }

    /**
     * Validates the reset token and extracts the userId.
     * Throws exception if token is invalid or expired.
     */
    private String validateResetTokenAndGetUserId(String resetToken) {
        // Implement token validation logic here.
        // This can involve verifying signature, issuer, expiry, and extracting user info.
        // For example, decode JWT and validate claims.

        // For demonstration, assume token is userId (replace with real validation):
        if (!StringUtils.hasText(resetToken)) {
            throw new IllegalArgumentException("Invalid reset token");
        }
        // TODO: Replace the below with real token validation and extraction
        String userId = resetToken; // Placeholder

        // Optionally verify token expiry, signature, etc.

        return userId;
    }
}
