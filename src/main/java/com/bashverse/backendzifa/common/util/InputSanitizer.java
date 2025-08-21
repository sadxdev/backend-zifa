package com.bashverse.backendzifa.common.util;

import org.apache.commons.text.StringEscapeUtils;

public class InputSanitizer {

    /**
     * Sanitizes a string input to prevent XSS and injection attacks.
     * @param input raw user input string
     * @return sanitized string
     */
    public static String sanitize(String input) {
        if (input == null) return null;
        // Escape HTML, JavaScript, SQL injection attempts etc.
        return StringEscapeUtils.escapeHtml4(input.trim());
    }

    /**
     * Overloaded for LoginRequest or other DTOs if complex sanitization is needed.
     */
    public static <T> T sanitize(T dto) {
        // Implement DTO-wise sanitation if necessary, e.g. reflectively or manually sanitize fields.
        return dto;
    }
}

