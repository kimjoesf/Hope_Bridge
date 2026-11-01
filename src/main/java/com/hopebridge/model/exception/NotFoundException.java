package com.hopebridge.model.exception;

/**
 * Thrown when someone asks for a record that does not exist (or that they are not allowed to see).
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
