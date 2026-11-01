package com.hopebridge.model.support;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Writes amounts the way we say them in messages to people: "$1,250.00".
 */
public final class Money {

    private Money() {
    }

    // NumberFormat is not thread-safe, so we create one per call instead of sharing a static instance.
    public static String format(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }
}