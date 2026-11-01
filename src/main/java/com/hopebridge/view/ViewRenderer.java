package com.hopebridge.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/** 
 * VIEW - turns a template name plus the data a controller prepared into HTML.
 *
 * Controllers never write HTML themselves; they pick a template and hand over a model.
 * Templates live in src/main/resources/templates and are plain HTML with a few th: attributes, i am really not professional in this th: vibe but it would be easier than react for this project.
 */
public class ViewRenderer {

    private final TemplateEngine engine = new TemplateEngine();
    private final ViewFormatter formatter = new ViewFormatter();
    private volatile JakartaServletWebApplication webApplication;   // it is volatile because it is accessed by multiple threads by default so that incase any request changes the web application, it will be reflected in all threads .

    // the constructor simply tells the engine where to find templates and whether to cache them or not so it is just a simple configuration .
    public ViewRenderer(boolean cacheTemplates) {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(cacheTemplates);
        engine.setTemplateResolver(resolver);
    }

    // this is the page builder it simply builds the page inject the data the the format .
    // Map<String, Object> model, model here is a bag for data results from an action or a service the controller passes it to the view, all what relates it to the model in MVC is that for sure the object inside will have a type in the model folder .
    public void render(String template, Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // A web-aware context lets templates build links with @{...}, what i understood is that it 
        // helps getting a correct link for example is inside a subfolder called app okay 
        // now an href sends the user to /dashboard because the app is runninng on a webserver 
        // it will forward localhost:8080/dashboard while it should be localhost:8080/app/dashboard 
        // so this is to help build the correct links.
        WebContext context = new WebContext(webApplication(request).buildExchange(request, response), Locale.US, model);
        // Every template can format money, dates and labels through ${fmt...}.
        context.setVariable("fmt", formatter);
        // this is just telling the browser that the response is html and the charset is utf-8 .
        response.setContentType("text/html;charset=UTF-8");
        // here is where all the job happens it merges the template with the context, generate the final page and sends it to the browser .
        engine.process(template, context, response.getWriter());
    }

    // This method is a helper that provides the JakartaServletWebApplication object needed by Thymeleaf, what is Thymeleaf i am still trying to understand .
    private JakartaServletWebApplication webApplication(HttpServletRequest request) {
        if (webApplication == null) {
            synchronized (this) {
                if (webApplication == null) {
                    webApplication = JakartaServletWebApplication.buildApplication(request.getServletContext());
                }
            }
        }
        return webApplication;
    }
}
