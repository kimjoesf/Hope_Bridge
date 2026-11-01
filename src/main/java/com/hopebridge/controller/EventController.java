package com.hopebridge.controller;

import com.hopebridge.model.entity.Event;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * CONTROLLER - browsing events, registering, and the user's own tickets.
 */
public class EventController extends BaseController {

    private final EventService eventService;

    public EventController(ControllerSupport support, EventService eventService) {
        super(support);
        this.eventService = eventService;
        get("/events", this::list);
        post("/events/register", this::register);
        post("/events/unregister", this::unregister);
        get("/panel/events", this::myTickets);
    }

    // Public page; when someone is signed in we also mark the events they already joined.
    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Integer> registeredIds = currentUser(request)
                .map(user -> eventService.registeredEventIds(user.getId()))
                .orElse(List.of());
        render(request, response, "events/list", model(
                "events", eventService.upcoming(),
                "registeredIds", registeredIds));
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireUser(request);
        String ticketCode = eventService.register(requiredId(request, "eventId"), user);
        success(request, "You're in! Your ticket code is " + ticketCode + ".");
        redirect(response, "/panel/events");
    }

    private void unregister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireUser(request);
        Event event = eventService.unregister(requiredId(request, "eventId"), user);
        success(request, "Your seat for \"" + event.getTitle() + "\" was released.");
        redirect(response, "/panel/events");
    }

    private void myTickets(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireUser(request);
        render(request, response, "panel/events", model("registrations", eventService.registrationsFor(user.getId())));
    }
}
