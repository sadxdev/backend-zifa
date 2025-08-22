package com.bashverse.backendzifa.auth.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for reset-password API.
 * Contains the new password and the reset token received by the user.
 */
@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token must not be blank")
    private String resetToken;

    @NotBlank(message = "New password must not be blank")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String newPassword;
}
