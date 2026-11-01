package com.hopebridge.model.support;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Writes dates the way we say them in messages to people: "Sat, Nov 14 at 10:00 AM".
 */
public final class Dates {

    private static final DateTimeFormatter FRIENDLY = DateTimeFormatter.ofPattern("EEE, MMM d 'at' h:mm a", Locale.US);

    private Dates() {
    }

    public static String friendly(LocalDateTime dateTime) {
        return FRIENDLY.format(dateTime);
    }
}
