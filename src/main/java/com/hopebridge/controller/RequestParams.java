package com.hopebridge.controller;

import com.hopebridge.model.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Reads typed values out of the request. Turning HTTP text into numbers and dates is
 * the controller's job; deciding whether those values make sense is the model's.
 *
 * An empty field becomes null. A field that is filled in but unreadable becomes a ValidationException instead of a crash.
 */
final class RequestParams {

    private RequestParams() {
    }

    static String text(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? null : value.trim();
    }

    static Integer integer(HttpServletRequest request, String name) {
        return parse(request, name, Integer::valueOf, "a whole number");
    }

    static BigDecimal decimal(HttpServletRequest request, String name) {
        return parse(request, name, BigDecimal::new, "a number");
    }

    static LocalDate date(HttpServletRequest request, String name) {
        return parse(request, name, LocalDate::parse, "a date");
    }

    static LocalDateTime dateTime(HttpServletRequest request, String name) {
        return parse(request, name, LocalDateTime::parse, "a date and time");
    }

    // A checkbox is only sent when it is ticked.
    static boolean checked(HttpServletRequest request, String name) {
        return request.getParameter(name) != null;
    }

    static Set<String> values(HttpServletRequest request, String name) {
        String[] values = request.getParameterValues(name);
        return values == null ? Set.of() : new LinkedHashSet<>(Arrays.asList(values));
    }

    static List<Integer> integers(HttpServletRequest request, String name) {
        try {
            return values(request, name).stream().map(Integer::valueOf).toList();
        } catch (NumberFormatException e) {
            throw new ValidationException("Some of the selected options are not valid.");
        }
    }

    private static <T> T parse(HttpServletRequest request, String name, Function<String, T> parser, String expected) {
        String value = text(request, name);
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return parser.apply(value);
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new ValidationException("\"" + value + "\" is not " + expected + ".");
        }
    }
}
