package com.hopebridge.controller;

import com.hopebridge.model.service.AuthService;
import com.hopebridge.model.service.NotificationService;
import com.hopebridge.view.ViewRenderer;

/**
 * the idea of this class is simply there is a shared parameters across all controllers okay, and we can't inherit from 2 classes the base and the support class so this an introduced idea i have encountered it for the first time, so it is just a try to see if it works . 
 * The few parameters every controller needs, bundled so each constructor only takes a few extra argument.
 */
public record ControllerSupport(ViewRenderer views, AuthService authService, NotificationService notificationService) {
}
