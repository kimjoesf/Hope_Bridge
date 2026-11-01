package com.hopebridge.controller;

import com.hopebridge.model.dto.VolunteerSummary;
import com.hopebridge.model.entity.LookupTable;
import com.hopebridge.model.entity.Role;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.entity.VolunteerShift;
import com.hopebridge.model.exception.ValidationException;
import com.hopebridge.model.service.EventService;
import com.hopebridge.model.service.LookupService;
import com.hopebridge.model.service.VolunteerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

/**
 * CONTROLLER - the volunteer-only part of the panel: skills, hours and the certificate.
 */
public class VolunteerController extends BaseController {

    private static final String PROFILE = "/panel/volunteer";
    private static final String HOURS = "/panel/hours";

    private final VolunteerService volunteerService;
    private final LookupService lookupService;
    private final EventService eventService;

    public VolunteerController(ControllerSupport support, VolunteerService volunteerService,
                               LookupService lookupService, EventService eventService) {
        super(support);
        this.volunteerService = volunteerService;
        this.lookupService = lookupService;
        this.eventService = eventService;
        get(PROFILE, this::showProfile);
        post(PROFILE, this::saveProfile);
        get(HOURS, this::showHours);
        post(HOURS, this::logHours);
        get("/panel/certificate", this::certificate);
    }

    private void showProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User volunteer = requireRole(request, Role.VOLUNTEER);
        render(request, response, "panel/volunteer", model(
                "skills", lookupService.active(LookupTable.SKILLS),
                "slots", lookupService.active(LookupTable.AVAILABILITY_SLOTS),
                "selectedSkills", volunteerService.skillIds(volunteer.getId()),
                "selectedSlots", volunteerService.slotIds(volunteer.getId())));
    }

    private void saveProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User volunteer = requireRole(request, Role.VOLUNTEER);
        volunteerService.updateProfile(volunteer,
                RequestParams.integers(request, "skillIds"),
                RequestParams.integers(request, "slotIds"));
        success(request, "Your skills and availability were updated.");
        redirect(response, PROFILE);
    }

    private void showHours(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User volunteer = requireRole(request, Role.VOLUNTEER);
        render(request, response, "panel/hours", model(
                "shifts", volunteerService.shifts(volunteer.getId()),
                "summary", volunteerService.summary(volunteer.getId()),
                "events", eventService.all(),
                "today", LocalDate.now()));
    }

    private void logHours(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User volunteer = requireRole(request, Role.VOLUNTEER);
        VolunteerShift shift = volunteerService.logHours(volunteer,
                RequestParams.date(request, "workDate"),
                RequestParams.decimal(request, "hours"),
                RequestParams.integer(request, "eventId"),
                RequestParams.text(request, "note"));
        success(request, "Logged " + shift.getHours().stripTrailingZeros().toPlainString() + " hours. Thank you for your time!");
        redirect(response, HOURS);
    }

    private void certificate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User volunteer = requireRole(request, Role.VOLUNTEER);
        VolunteerSummary summary = volunteerService.summary(volunteer.getId());
        if (!summary.hasHours()) {
            throw new ValidationException("Log your first volunteer hours to unlock your certificate.");
        }
        render(request, response, "panel/certificate", model(
                "summary", summary,
                "certificateNumber", volunteerService.certificateNumber(volunteer)));
    }
}
