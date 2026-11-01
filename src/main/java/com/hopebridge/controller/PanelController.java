package com.hopebridge.controller;

import com.hopebridge.model.entity.EventRegistration;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.service.DonationService;
import com.hopebridge.model.service.EventService;
import com.hopebridge.model.service.ImpactService;
import com.hopebridge.model.service.LookupService;
import com.hopebridge.model.service.VolunteerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * CONTROLLER - the User Control Panel's overview page.
 * One page for everyone, but each role sees the cards that matter to them.
 */
public class PanelController extends BaseController {

    private static final int PREVIEW_SIZE = 4;

    private final DonationService donationService;
    private final EventService eventService;
    private final VolunteerService volunteerService;
    private final ImpactService impactService;
    private final LookupService lookupService;

    public PanelController(ControllerSupport support, DonationService donationService, EventService eventService,
                           VolunteerService volunteerService, ImpactService impactService, LookupService lookupService) {
        super(support);
        this.donationService = donationService;
        this.eventService = eventService;
        this.volunteerService = volunteerService;
        this.impactService = impactService;
        this.lookupService = lookupService;
        get("/panel", this::dashboard);
    }

    private void dashboard(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = requireUser(request);
        List<EventRegistration> upcomingTickets = eventService.registrationsFor(user.getId()).stream()
                .filter(registration -> !registration.getEvent().isPast())
                .limit(PREVIEW_SIZE)
                .toList();

        Map<String, Object> model = model(
                "latestNotifications", support.notificationService().latest(user.getId(), PREVIEW_SIZE),
                "registrations", upcomingTickets);

        if (user.isStaff()) {
            model.put("impact", impactService.snapshot());
            model.put("recentDonations", donationService.recent(6));
            model.put("lookupCounts", lookupService.counts());
            model.put("upcomingEvents", eventService.upcoming().stream().limit(PREVIEW_SIZE).toList());
        } else {
            model.put("donorSummary", donationService.summaryFor(user));
            model.put("recentDonations", donationService.historyFor(user.getId()).stream().limit(3).toList());
        }
        if (user.isVolunteer()) {
            model.put("volunteerSummary", volunteerService.summary(user.getId()));
            model.put("skills", volunteerService.skillNames(user.getId()));
        }
        render(request, response, "panel/dashboard", model);
    }
}
