package com.hopebridge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * One-time messages that survive a redirect ("Saved!", "Please choose a cause...").
 *
 * After a POST we always redirect (so refreshing never re-submits a form). The message
 * and what the user typed are parked in the session, shown on the next page, then removed.
 */
final class Flash {

    private static final String MESSAGES = "flash.messages";
    private static final String OLD_INPUT = "flash.old";
    // Never echo these back into a form.
    private static final Set<String> SECRET_FIELDS = Set.of("password", "_csrf");

    private Flash() {
    }

    static void success(HttpServletRequest request, String message) {
        put(request, "success", message);
    }

    static void error(HttpServletRequest request, String message) {
        put(request, "error", message);
    }

    // Remember what was typed so a form with a mistake comes back filled in, not empty.
    static void keepInput(HttpServletRequest request) {
        Map<String, String> old = new HashMap<>();
        request.getParameterMap().forEach((name, values) -> {
            if (!SECRET_FIELDS.contains(name)) {
                old.put(name, String.join(",", values));
            }
        });
        request.getSession().setAttribute(OLD_INPUT, old);
    }

    static Map<String, String> takeMessages(HttpSession session) {
        return take(session, MESSAGES);
    }

    static Map<String, String> takeOldInput(HttpSession session) {
        return take(session, OLD_INPUT);
    }

    @SuppressWarnings("unchecked")
    private static void put(HttpServletRequest request, String kind, String message) {
        HttpSession session = request.getSession();
        Map<String, String> messages = (Map<String, String>) session.getAttribute(MESSAGES);
        if (messages == null) {
            messages = new HashMap<>();
            session.setAttribute(MESSAGES, messages);
        }
        messages.put(kind, message);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> take(HttpSession session, String key) {
        Map<String, String> value = (Map<String, String>) session.getAttribute(key);
        session.removeAttribute(key);
        return value == null ? Map.of() : value;
    }
}
