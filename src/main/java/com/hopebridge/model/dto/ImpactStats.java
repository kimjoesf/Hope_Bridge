package com.hopebridge.model.dto;

import java.math.BigDecimal;

/**
 * The headline numbers of the organization.
 */
public class ImpactStats {

    private final BigDecimal totalRaised;
    private final int donorCount;
    private final BigDecimal volunteerHours;
    private final int upcomingEvents;
    private final int activeCampaigns;
    private final int openDonations;

    public ImpactStats(BigDecimal totalRaised, int donorCount, BigDecimal volunteerHours,
                       int upcomingEvents, int activeCampaigns, int openDonations) {
        this.totalRaised = totalRaised;
        this.donorCount = donorCount;
        this.volunteerHours = volunteerHours;
        this.upcomingEvents = upcomingEvents;
        this.activeCampaigns = activeCampaigns;
        this.openDonations = openDonations;
    }

    public BigDecimal getTotalRaised() {
        return totalRaised;
    }

    public int getDonorCount() {
        return donorCount;
    }

    public BigDecimal getVolunteerHours() {
        return volunteerHours;
    }

    public int getUpcomingEvents() {
        return upcomingEvents;
    }

    // UNUSED
    // public int getActiveCampaigns() {
    //     return activeCampaigns;
    // }

    // Checks not cleared yet and goods not dropped off yet - things staff still need to follow up.
    public int getOpenDonations() {
        return openDonations;
    }
}
