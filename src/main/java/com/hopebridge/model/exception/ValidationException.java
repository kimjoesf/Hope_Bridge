package com.hopebridge.model.exception;

/**
 * Thrown when a business rule says "no" - an empty name, a full event, a wrong amount...
 * The message is written for the end user, because it ends up on the screen.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
