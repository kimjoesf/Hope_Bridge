package com.hopebridge.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * VIEW - presentation helpers available in every template as ${fmt}.
 *
 * Anything that is purely about how something LOOKS (currency symbols, date styles,
 * which badge color a status gets, which icon a channel uses) belongs here, so the
 * model stays free of display concerns and the templates stay free of logic.
 */
public class ViewFormatter {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.US);
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("EEE, MMM d · h:mm a", Locale.US);
    // private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("d", Locale.US);
    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("MMM", Locale.US);
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private static final Map<String, String> STATUS_TONES = Map.of(
            "CONFIRMED", "success",
            "PENDING_CLEARANCE", "warning",
            "AWAITING_DROP_OFF", "info");

    private static final Map<String, String> ICONS = Map.ofEntries(
            Map.entry("EMAIL", "mail"),
            Map.entry("SMS", "message"),
            Map.entry("SOCIAL", "share"),
            Map.entry("ONLINE", "card"),
            Map.entry("CHECK", "receipt"),
            Map.entry("IN_KIND", "gift"),
            Map.entry("donation-categories", "heart"),
            Map.entry("skills", "sparkles"),
            Map.entry("event-categories", "calendar"),
            Map.entry("availability-slots", "clock"));

    public String money(BigDecimal amount) {
        return amount == null ? "" : NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }

    // "$14.5K" style, for the big headline numbers.
    public String compactMoney(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        if (amount.compareTo(BigDecimal.valueOf(1_000_000)) >= 0) {
            return "$" + trim(amount.divide(BigDecimal.valueOf(1_000_000), 1, RoundingMode.DOWN)) + "M";
        }
        if (amount.compareTo(BigDecimal.valueOf(1_000)) >= 0) {
            return "$" + trim(amount.divide(BigDecimal.valueOf(1_000), 1, RoundingMode.DOWN)) + "K";
        }
        return "$" + trim(amount.setScale(0, RoundingMode.DOWN));
    }

    // 12.50 -> "12.5", 4.00 -> "4"
    public String number(BigDecimal value) {
        return value == null ? "0" : trim(value);
    }

    public String date(LocalDate date) {
        return date == null ? "" : DATE.format(date);
    }

    public String date(LocalDateTime dateTime) {
        return dateTime == null ? "" : DATE.format(dateTime);
    }

    public String dateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : DATE_TIME.format(dateTime);
    }

    // UNUSED
    // public String time(LocalDateTime dateTime) {
    //     return dateTime == null ? "" : TIME.format(dateTime);
    // }

    public String day(LocalDateTime dateTime) {
        return DAY.format(dateTime);
    }

    public String month(LocalDateTime dateTime) {
        return MONTH.format(dateTime).toUpperCase(Locale.US);
    }

    // The value format an <input type="datetime-local"> expects.
    public String inputDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : INPUT_DATE_TIME.format(dateTime);
    }

    public String today() {
        return DATE.format(LocalDate.now());
    }

    public String timeAgo(LocalDateTime moment) {
        if (moment == null) {
            return "";
        }
        long minutes = Duration.between(moment, LocalDateTime.now()).toMinutes();
        if (minutes < 1) {
            return "just now";
        }
        if (minutes < 60) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }
        long days = hours / 24;
        if (days < 30) {
            return days + (days == 1 ? " day ago" : " days ago");
        }
        return date(moment);
    }

    public String initials(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "?";
        }
        String[] parts = fullName.trim().split("\\s+");
        String first = parts[0].substring(0, 1);
        String last = parts.length > 1 ? parts[parts.length - 1].substring(0, 1) : "";
        return (first + last).toUpperCase(Locale.US);
    }

    // all it does is to change a DB code to human code for ex: "PENDING_CLEARANCE" -> "Pending clearance"
    public String humanize(String code) {
        if (code == null || code.isBlank()) {
            return "";
        }
        String words = code.replace('_', ' ').toLowerCase(Locale.US);
        return Character.toUpperCase(words.charAt(0)) + words.substring(1);
    }

    // same as the above but for lists like "COVER_FEES,EMPLOYER_MATCH" -> ["Cover fees", "Employer match"]
    public List<String> humanizeList(String codes) {
        if (codes == null || codes.isBlank()) {
            return List.of();
        }
        return Arrays.stream(codes.split(",")).map(this::humanize).toList();
    }

    public String statusTone(String status) {
        return STATUS_TONES.getOrDefault(status, "neutral");
    }

    public String icon(String key) {
        return ICONS.getOrDefault(key, "dot");
    }

    private static String trim(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }
}
