package com.hopebridge.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * filter is a layer lies between the client browser and the controllers and the doFilter method, is the method that run auto whenver a request pass by that filter .
 * this fliter Sends anyone who is not signed in to the login page, remembering where they wanted to go.
 * Which ROLE may open a page is checked inside each controller.
 */
public class AuthFilter extends HttpFilter {

    public static final String USER_ID = "auth.userId";  // JUST A KEY TO LOOK UP THE USER ID IN THE SESSION .

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // this gets the current http session and false means don't create a new one if it doesn't exist .
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(USER_ID) != null) {
            chain.doFilter(request, response);
            return;
        }
        // After signing in we bring them back - but only to pages they can open with a plain GET.
        // this string remebers the page the users tries to visit 
        String target = "GET".equals(request.getMethod())? request.getRequestURI() + (request.getQueryString() == null ? "" : "?" + request.getQueryString()): "/panel";  //  for example login?next=/donate?amount=50
        response.sendRedirect("/login?next=" + URLEncoder.encode(target, StandardCharsets.UTF_8));
    }
}
