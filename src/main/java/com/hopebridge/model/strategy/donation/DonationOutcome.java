package com.hopebridge.model.strategy.donation;

/**
 * What a donation method decided: the status the donation starts in, the reference
 * we print on the receipt, and a short message for the donor.
 */
public record DonationOutcome(String status, String reference, String message) {
}
