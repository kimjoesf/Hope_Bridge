package com.hopebridge.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * CONTROLLER - the friendly page shown for 403, 404 and 500 errors.
 * Tomcat forwards here; the template picks the right words for each status.
 */
public class ErrorController extends BaseController {

    public static final String PATH = "/error";

    public ErrorController(ControllerSupport support) {
        super(support);
        // The original request may have been a GET or a POST, so we answer both.
        get(PATH, this::show);
        post(PATH, this::show);
    }

    private void show(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int code = status instanceof Integer value ? value : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        response.setStatus(code);
        Map<String, Object> model = model("status", code, "detail", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        try {
            render(request, response, "error", model);
        } catch (RuntimeException e) {
            // If the database is what failed, we still want to show something kind.
            renderWithoutUser(request, response, "error", model);
        }
    }
}
