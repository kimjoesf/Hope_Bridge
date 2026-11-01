package com.hopebridge.model.entity;

/**
 * Where a donation stands right after it was received.
 * Each donation method strategy decides which of these applies.
 */
public final class DonationStatus {

    public static final String CONFIRMED = "CONFIRMED";                     // donation is confirmed and processed .
    public static final String PENDING_CLEARANCE = "PENDING_CLEARANCE";     // donation is registered and handed from the donor but still not received by the organization bank account for instance .
    public static final String AWAITING_DROP_OFF = "AWAITING_DROP_OFF";     // mostly for in-kind donations or if the donation was registerd but still not handed .

}
