package com.hopebridge.model.support;

import com.hopebridge.model.exception.ValidationException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * The small checks every service keeps needing ("is this filled in?", "is this
 * a positive amount?"), written once. Each one either returns the cleaned value
 * or throws a ValidationException with a message the user can read.
 */
public final class Rules {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("1000000");

    private Rules() {
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new ValidationException(message);
        }
    }

    // Trims the text, makes sure it is there and not too long, then hands it back.
    public static String requireText(String value, String field, int maxLength) {
        String text = value == null ? "" : value.trim();
        require(!text.isEmpty(), field + " is required.");
        require(text.length() <= maxLength, field + " must be at most " + maxLength + " characters.");
        return text;
    }

    // Same idea for fields that may stay empty: blank becomes null.
    public static String optionalText(String value, String field, int maxLength) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            return null;
        }
        require(text.length() <= maxLength, field + " must be at most " + maxLength + " characters.");
        return text;
    }

    public static BigDecimal requireAmount(BigDecimal amount, String field) {
        require(amount != null, field + " is required.");
        require(amount.signum() > 0, field + " must be greater than zero.");
        require(amount.compareTo(MAX_AMOUNT) <= 0, field + " looks too large. Please contact us for gifts over $1,000,000.");
        return amount;
    }

    public static String requireEmail(String value) {
        String email = requireText(value, "Email", 160).toLowerCase();
        require(EMAIL.matcher(email).matches(), "Please enter a valid email address.");
        return email;
    }
}