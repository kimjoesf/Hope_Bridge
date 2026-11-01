package com.hopebridge.controller;

/**
 * Thrown when a signed-in user opens a page their role does not allow. It becomes a 403 page.
 *  very simple protection proxy .
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException() {
        super("You do not have access to this page.");
    }
}