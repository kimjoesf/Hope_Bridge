package com.hopebridge.server;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Serves the CSS, JavaScript and images under src/main/resources/static.
 */
public class StaticResourceServlet extends HttpServlet {

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            "css", "text/css; charset=UTF-8",
            "js", "text/javascript; charset=UTF-8",
            "svg", "image/svg+xml",
            "png", "image/png",
            "jpg", "image/jpeg",
            "webp", "image/webp",
            "ico", "image/x-icon",
            "woff2", "font/woff2");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        // Refuse anything that tries to climb out of the static folder.
        if (path == null || path.contains("..")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try (InputStream file = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            if (file == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String extension = path.substring(path.lastIndexOf('.') + 1);
            response.setContentType(CONTENT_TYPES.getOrDefault(extension, "application/octet-stream"));
            response.setHeader("Cache-Control", "public, max-age=3600");
            file.transferTo(response.getOutputStream());
        }
    }
}
