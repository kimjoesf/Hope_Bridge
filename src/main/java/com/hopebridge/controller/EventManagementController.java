package com.hopebridge.controller;

import com.hopebridge.model.dto.EventForm;
import com.hopebridge.model.entity.Event;
import com.hopebridge.model.entity.LookupTable;
import com.hopebridge.model.entity.Role;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.service.EventService;
import com.hopebridge.model.service.LookupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * CONTROLLER - staff creating, editing and reminding people about events.
 * Saving goes through EventService, which posts a bulletin on the EventBoard (Observer).
 */
public class EventManagementController extends BaseController {

    private static final String LIST = "/panel/manage/events";

    private final EventService eventService;
    private final LookupService lookupService;

    public EventManagementController(ControllerSupport support, EventService eventService, LookupService lookupService) {
        super(support);
        this.eventService = eventService;
        this.lookupService = lookupService;
        get(LIST, this::list);
        get(LIST + "/edit", this::showForm);
        post(LIST + "/save", this::save);
        post(LIST + "/remind", this::remind);
    }

    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireRole(request, Role.STAFF);
        render(request, response, "panel/manage-events", model("events", eventService.all()));
    }

    // No id means "new event"; with an id we load the event to edit.
    private void showForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireRole(request, Role.STAFF);
        Integer id = RequestParams.integer(request, "id");
        render(request, response, "panel/event-form", model(
                "event", id == null ? null : eventService.find(id),
                "categories", lookupService.active(LookupTable.EVENT_CATEGORIES)));
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User staff = requireRole(request, Role.STAFF);
        EventForm form = new EventForm(
                RequestParams.integer(request, "id"),
                RequestParams.text(request, "title"),
                RequestParams.text(request, "summary"),
                RequestParams.integer(request, "categoryId"),
                RequestParams.text(request, "location"),
                RequestParams.dateTime(request, "startsAt"),
                RequestParams.integer(request, "capacity"));
        Event event = eventService.save(form, staff);
        success(request, form.isNew()
                ? "\"" + event.getTitle() + "\" is published, and every volunteer has been invited to help."
                : "\"" + event.getTitle() + "\" was updated, and everyone registered has been told.");
        redirect(response, LIST);
    }

    private void remind(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireRole(request, Role.STAFF);
        Event event = eventService.sendReminder(requiredId(request, "eventId"));
        success(request, "Reminder sent to the " + event.getRegisteredCount() + " people registered for \"" + event.getTitle() + "\".");
        redirect(response, LIST);
    }
}
