package com.hopebridge.model.support;

import java.security.SecureRandom;

/**
 * Generates the short human-friendly codes we print on screens and receipts
 * (transaction references, ticket codes...).
 */
public final class Codes {

    // No 0/O or 1/I, so nobody misreads a code over the phone.
    private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    private Codes() {
    }

    public static String random(String prefix, int length) {
        StringBuilder code = new StringBuilder(prefix);
        for (int i = 0; i < length; i++) {
            code.append(ALPHABET[RANDOM.nextInt(ALPHABET.length)]);
        }
        return code.toString();
    }
}