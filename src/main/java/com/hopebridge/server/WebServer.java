package com.hopebridge.server;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 * A thin wrapper around embedded Tomcat.
 *
 * This is plumbing, not part of M, V or C: it only knows how to plug servlets and
 * filters into a running web server. Which URL goes to which controller is decided
 * by the controllers themselves.
 */
public class WebServer {

    private static final int SESSION_TIMEOUT_MINUTES = 60;

    private final Tomcat tomcat = new Tomcat();
    private final Context context;
    private final int port;

    public WebServer(int port) {
        this.port = port;
        Path workDir = Path.of("target", "tomcat").toAbsolutePath();
        tomcat.setBaseDir(workDir.toString());
        tomcat.setPort(port);
        tomcat.getConnector();

        context = tomcat.addContext("", workDir.toString());
        context.setRequestCharacterEncoding("UTF-8");
        context.setResponseCharacterEncoding("UTF-8");
        context.setSessionTimeout(SESSION_TIMEOUT_MINUTES);

        // Sessions live in memory only; we do not want Tomcat writing them to disk on shutdown.
        StandardManager sessions = new StandardManager();
        sessions.setPathname(null);
        context.setManager(sessions);

        // Session cookies are HttpOnly by default; SameSite=Lax adds a second line of defence against CSRF.
        Rfc6265CookieProcessor cookies = new Rfc6265CookieProcessor();
        cookies.setSameSiteCookies("lax");
        context.setCookieProcessor(cookies);

        mount("static", new StaticResourceServlet(), List.of("/static/*"));
    }

    public void mount(String name, HttpServlet servlet, Collection<String> urlPatterns) {
        Tomcat.addServlet(context, name, servlet);
        urlPatterns.forEach(pattern -> context.addServletMappingDecoded(pattern, name));
    }

    public void addFilter(String name, Filter filter, String... urlPatterns) {
        FilterDef definition = new FilterDef();
        definition.setFilterName(name);
        definition.setFilter(filter);
        context.addFilterDef(definition);

        FilterMap mapping = new FilterMap();
        mapping.setFilterName(name);
        for (String pattern : urlPatterns) {
            mapping.addURLPattern(pattern);
        }
        context.addFilterMap(mapping);
    }

    public void addErrorPage(int statusCode, String location) {
        ErrorPage page = new ErrorPage();
        page.setErrorCode(statusCode);
        page.setLocation(location);
        context.addErrorPage(page);
    }

    // Any uncaught exception ends up on the same friendly page as a 500.
    public void addExceptionPage(String location) {
        ErrorPage page = new ErrorPage();
        page.setExceptionType(Throwable.class.getName());
        page.setLocation(location);
        context.addErrorPage(page);
    }

    public void start() throws LifecycleException {
        tomcat.start();
        System.out.println();
        System.out.println("  HopeBridge is running at  http://localhost:" + port);
        System.out.println("  Press Ctrl+C to stop.");
        System.out.println();
        tomcat.getServer().await();
    }
}
