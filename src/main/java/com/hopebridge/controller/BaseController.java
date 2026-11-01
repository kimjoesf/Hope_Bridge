package com.hopebridge.controller;

import com.hopebridge.controller.filter.AuthFilter;
import com.hopebridge.controller.filter.CsrfFilter;
import com.hopebridge.model.entity.Role;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.exception.NotFoundException;
import com.hopebridge.model.exception.ValidationException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * CONTROLLER - what every controller shares.
 *
 * A controller's job is small and always the same: read the request, ask the model
 * to do the work, then pick a view (or redirect). Everything around that - routing,
 * flash messages, knowing who is signed in, turning model errors into friendly
 * pages - is written once here.
 *
 * Each subclass declares its routes in its constructor, e.g.
 *     get("/panel/profile", this::showProfile);
 *     post("/panel/profile", this::saveProfile);
 */
public abstract class BaseController extends HttpServlet {

    @FunctionalInterface
    protected interface Action {
        void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
    }

    private static final String CURRENT_USER = "request.currentUser";

    protected final ControllerSupport support;
    private final Map<String, Action> getRoutes = new LinkedHashMap<>();  // linked hash map remembers the exact order in which you .put() the routes .
    private final Map<String, Action> postRoutes = new LinkedHashMap<>();

    protected BaseController(ControllerSupport support) {
        this.support = support;
    }

    protected final void get(String path, Action action) {
        getRoutes.put(path, action);
    }

    protected final void post(String path, Action action) {
        postRoutes.put(path, action);
    }

    // The web server mounts this controller on exactly these URLs.
    public final Set<String> paths() {
        Set<String> paths = new LinkedHashSet<>(getRoutes.keySet());
        paths.addAll(postRoutes.keySet());
        return paths;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Action> routes = "POST".equals(request.getMethod()) ? postRoutes : getRoutes;
        Action action = routes.get(request.getServletPath());  // it returns the action because the put function inside the get returns the value associated with the given key .
        if (action == null) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        try {
            action.handle(request, response);
        } catch (ValidationException e) {
            // A business rule said no: go back to the form, show why, and keep what was typed.
            Flash.error(request, e.getMessage());
            Flash.keepInput(request);
            redirect(response, previousPage(request));
        } catch (NotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (AccessDeniedException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    protected final void render(HttpServletRequest request, HttpServletResponse response,
                                String view, Map<String, Object> model) throws IOException {
        renderPage(request, response, view, model, true);
    }

    // For the error page: if the database is the thing that broke, we cannot look the user up.
    protected final void renderWithoutUser(HttpServletRequest request, HttpServletResponse response,
                                           String view, Map<String, Object> model) throws IOException {
        renderPage(request, response, view, model, false);
    }

    private void renderPage(HttpServletRequest request, HttpServletResponse response,
                            String view, Map<String, Object> model, boolean withUser) throws IOException {
        Map<String, Object> data = new HashMap<>(model);
        HttpSession session = request.getSession();
        data.put("csrf", CsrfFilter.tokenFor(session));
        data.put("flash", Flash.takeMessages(session));
        data.put("old", Flash.takeOldInput(session));
        data.put("path", request.getServletPath());
        if (withUser) {
            currentUser(request).ifPresent(user -> {
                data.put("currentUser", user);
                data.put("unreadCount", support.notificationService().unreadCount(user.getId()));
            });
        }
        support.views().render(view, data, request, response);
    }

    // Builds a model map from "key", value pairs. Unlike Map.of, null values are fine here.
    protected static Map<String, Object> model(Object... keysAndValues) {
        Map<String, Object> model = new HashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            model.put((String) keysAndValues[i], keysAndValues[i + 1]);
        }
        return model;
    }

    // Looked up once per request, then remembered on the request itself.
    protected final Optional<User> currentUser(HttpServletRequest request) {
        if (request.getAttribute(CURRENT_USER) instanceof User user) {
            return Optional.of(user);
        }
        HttpSession session = request.getSession(false);
        Integer userId = session == null ? null : (Integer) session.getAttribute(AuthFilter.USER_ID);
        Optional<User> user = userId == null ? Optional.empty() : support.authService().findById(userId);
        user.ifPresent(found -> request.setAttribute(CURRENT_USER, found));
        return user;
    }

    protected final User requireUser(HttpServletRequest request) {
        return currentUser(request).orElseThrow(AccessDeniedException::new);
    }

    protected final User requireRole(HttpServletRequest request, Role role) {
        User user = requireUser(request);
        if (user.getRole() != role) {
            throw new AccessDeniedException();
        }
        return user;
    }

    // A fresh session id on sign-in stops anyone who knew the old id from riding along (session fixation).
    protected final void signIn(HttpServletRequest request, User user) {
        if (request.getSession(false) != null) {
            request.changeSessionId();
        }
        request.getSession().setAttribute(AuthFilter.USER_ID, user.getId());
    }

    protected final void signOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    // For ids a page cannot work without: a missing or broken id simply means "no such page".
    protected final int requiredId(HttpServletRequest request, String name) {
        try {
            Integer id = RequestParams.integer(request, name);
            if (id != null) {
                return id;
            }
        } catch (ValidationException ignored) {
            // falls through to "not found"
        }
        throw new NotFoundException("Missing " + name);
    }

    protected final void success(HttpServletRequest request, String message) {
        Flash.success(request, message);
    }

    protected final void redirect(HttpServletResponse response, String location) throws IOException {
        response.sendRedirect(location);
    }

    // Only the path of the Referer is used, so we can never be tricked into redirecting to another site.
    private static String previousPage(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                URI uri = URI.create(referer);
                if (uri.getRawPath() != null && uri.getRawPath().startsWith("/")) {
                    return uri.getRawPath() + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
                }
            } catch (IllegalArgumentException ignored) {
                // a malformed header just sends the user home
            }
        }
        return "/";
    }
}