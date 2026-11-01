package com.hopebridge.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Protects every form against cross-site request forgery.
 *
 * Each session gets a secret token that our pages put in a hidden field. A POST that
 * does not carry the same token did not come from one of our pages, so it is refused.
 */
 
public class CsrfFilter extends HttpFilter {

    public static final String PARAMETER = "_csrf";
    private static final String SESSION_KEY = "csrf.token";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String tokenFor(HttpSession session) {
        String token = (String) session.getAttribute(SESSION_KEY);
        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(SESSION_KEY, token);
        }
        return token;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if ("POST".equals(request.getMethod()) && !hasValidToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Your session expired. Please go back, refresh the page and try again.");
            return;
        }
        chain.doFilter(request, response);
    }

    private static boolean hasValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String expected = session == null ? null : (String) session.getAttribute(SESSION_KEY);
        String actual = request.getParameter(PARAMETER);
        return expected != null && actual != null
                && MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }
}