package com.hopebridge.model.strategy.communication;

/**
 * Text helpers shared by the short-message strategies (SMS and social media),
 * so they do not each carry their own copy.
 */
final class MessageText {

    private MessageText() {
    }

    static String singleLine(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

    // Cuts the text to the limit and adds an ellipsis so the reader knows there was more.
    static String shorten(String text, int limit) {
        return text.length() <= limit ? text : text.substring(0, limit - 1).trim() + "…";
    }
}
