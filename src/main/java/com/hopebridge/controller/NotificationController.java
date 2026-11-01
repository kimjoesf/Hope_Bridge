package com.hopebridge.controller;

import com.hopebridge.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * CONTROLLER - the user's inbox: every message we sent them, in the format of its channel.
 */
public class NotificationController extends BaseController {

    private static final String PAGE = "/panel/notifications";

    public NotificationController(ControllerSupport support) {
        super(support);
        get(PAGE, this::inbox);
        post(PAGE + "/read", this::markAllRead);
    }

    private void inbox(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireUser(request);
        render(request, response, "panel/notifications", model(
                "notifications", support.notificationService().inbox(user.getId())));
    }

    private void markAllRead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireUser(request);
        support.notificationService().markAllRead(user.getId());
        success(request, "All caught up!");
        redirect(response, PAGE);
    }
}
