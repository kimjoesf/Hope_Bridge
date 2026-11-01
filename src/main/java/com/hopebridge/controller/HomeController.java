package com.hopebridge.controller;

import com.hopebridge.model.service.DonationService;
import com.hopebridge.model.service.EventService;
import com.hopebridge.model.service.ImpactService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * CONTROLLER - the public landing page.
 */
public class HomeController extends BaseController {

    private static final int FEATURED_EVENTS = 3;

    private final DonationService donationService;
    private final EventService eventService;
    private final ImpactService impactService;

    public HomeController(ControllerSupport support, DonationService donationService,
                          EventService eventService, ImpactService impactService) {
        super(support);
        this.donationService = donationService;
        this.eventService = eventService;
        this.impactService = impactService;
        // "" is the site root itself.
        get("", this::home);
    }

    private void home(HttpServletRequest request, HttpServletResponse response) throws IOException {
        render(request, response, "home", model(
                "impact", impactService.snapshot(),
                "campaigns", donationService.activeCampaigns(),
                "events", eventService.upcoming().stream().limit(FEATURED_EVENTS).toList()));
    }
}
