package com.hopebridge.model.strategy.donation;

import com.hopebridge.model.decorator.Contribution;
import com.hopebridge.model.dto.DonationRequest;

/**
 * STRATEGY - the Context (the "Payment" class of the lab's payment example).
 *
 * It holds a reference to a DonationMethod and simply delegates to it, acting as
 * the bridge between the service and whichever method the donor picked.
 * It never asks "is this a check?" - that is the whole point.
 */
public class DonationProcessor {

    private DonationMethod donationMethod;

    public DonationProcessor(DonationMethod donationMethod) {
        this.donationMethod = donationMethod;
    }

    // Swap the algorithm at runtime without touching this class.
    public void setDonationMethod(DonationMethod donationMethod) {
        this.donationMethod = donationMethod;
    }

    public DonationMethod getDonationMethod() {
        return donationMethod;
    }

    public DonationOutcome process(DonationRequest request) {
        return donationMethod.process(request);
    }

    public Contribution contributionFor(DonationRequest request) {
        return donationMethod.baseContribution(request);
    }
}
