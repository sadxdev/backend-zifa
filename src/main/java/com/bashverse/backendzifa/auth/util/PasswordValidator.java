package com.bashverse.backendzifa.auth.util;

import java.util.regex.Pattern;

/**
 * Utility class for validating passwords against enterprise policies.
 */
public class PasswordValidator {

    // At least one uppercase letter
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    // At least one lowercase letter
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    // At least one digit
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    // At least one special character
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9]");

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 64;

    /**
     * Validates that the password meets the policy requirements.
     *
     * @param password the password string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            return false;
        }
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return false;
        }
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return false;
        }
        if (!DIGIT_PATTERN.matcher(password).find()) {
            return false;
        }
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return false;
        }
        return true;
    }

    /**
     * Returns a human-readable message about password requirements.
     *
     * @return password policy message
     */
    public static String getPolicyMessage() {
        return String.format("Password must be %d-%d characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character.",
                MIN_LENGTH, MAX_LENGTH);
    }
}
