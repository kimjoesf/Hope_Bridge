package com.hopebridge.controller;

import com.hopebridge.model.entity.Role;
import com.hopebridge.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * CONTROLLER - staff adding new staff members.
 * Public sign-up never creates staff, so this page is the only way in - and only staff can open it.
 */
public class StaffController extends BaseController {

    private static final String PAGE = "/panel/staff";

    public StaffController(ControllerSupport support) {
        super(support);
        get(PAGE, this::show);
        post(PAGE, this::create);
    }

    private void show(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireRole(request, Role.STAFF);
        render(request, response, "panel/staff", model("staff", support.authService().staffMembers()));
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireRole(request, Role.STAFF);
        User member = support.authService().createStaff(
                RequestParams.text(request, "fullName"),
                RequestParams.text(request, "email"),
                request.getParameter("password"),
                RequestParams.text(request, "phone"));
        success(request, member.getFullName() + " was added to the team and can sign in right away.");
        redirect(response, PAGE);
    }
}
