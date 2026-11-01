package com.hopebridge.controller;

import com.hopebridge.model.entity.Role;
import com.hopebridge.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

/**
 * CONTROLLER - signing in, signing up and signing out.
 */
public class AuthController extends BaseController {

    private static final String HOME_AFTER_LOGIN = "/panel";

    public AuthController(ControllerSupport support) {
        super(support);
        get("/login", this::showLogin);
        post("/login", this::login);
        get("/register", this::showRegister);
        post("/register", this::register);
        post("/logout", this::logout);
    }

    private void showLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (currentUser(request).isPresent()) {
            redirect(response, HOME_AFTER_LOGIN);
            return;
        }
        render(request, response, "auth/login", model("next", safeNext(RequestParams.text(request, "next"))));
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = support.authService().authenticate(RequestParams.text(request, "email"), request.getParameter("password"));
        signIn(request, user);
        success(request, "Welcome back, " + user.getFirstName() + "!");
        redirect(response, safeNext(RequestParams.text(request, "next")));
    }

    private void showRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        render(request, response, "auth/register", model(
                "roles", Arrays.stream(Role.values()).filter(Role::isSelfRegistrable).toList(),
                "selectedRole", RequestParams.text(request, "role")));
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = support.authService().register(
                RequestParams.text(request, "fullName"),
                RequestParams.text(request, "email"),
                request.getParameter("password"),
                RequestParams.text(request, "role"),
                RequestParams.text(request, "phone"));
        signIn(request, user);
        success(request, "Your account is ready. Welcome to HopeBridge, " + user.getFirstName() + "!");
        redirect(response, HOME_AFTER_LOGIN);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        signOut(request);
        success(request, "You have been signed out. See you soon!");
        redirect(response, "/");
    }

    // Only local paths are accepted, so a crafted link cannot bounce people to another website after login.
    private static String safeNext(String next) {
        return next != null && next.startsWith("/") && !next.startsWith("//") ? next : HOME_AFTER_LOGIN;
    }
}
