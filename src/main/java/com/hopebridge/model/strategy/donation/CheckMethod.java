package com.hopebridge.model.strategy.donation;

import com.hopebridge.model.decorator.Contribution;
import com.hopebridge.model.decorator.MoneyContribution;
import com.hopebridge.model.dto.DonationRequest;
import com.hopebridge.model.entity.DonationStatus;
import com.hopebridge.model.support.Rules;

/**
 * Concrete strategy: a mailed check. We need the check number to match it when it
 * arrives, and it stays pending until the bank clears it.
 */
public class CheckMethod implements DonationMethod {

    @Override
    public String getCode() {
        return "CHECK";
    }

    @Override
    public String getLabel() {
        return "Check";
    }

    @Override
    public String getDescription() {
        return "Mail us a check. Confirmed once it clears (3-5 business days).";
    }

    @Override
    public Contribution baseContribution(DonationRequest request) {
        return new MoneyContribution(request.amount());
    }

    @Override
    public DonationOutcome process(DonationRequest request) {
        Rules.requireAmount(request.amount(), "Gift amount");
        String checkNumber = Rules.requireText(request.checkNumber(), "Check number", 12);
        Rules.require(checkNumber.matches("\\d{3,12}"), "A check number is 3 to 12 digits.");
        return new DonationOutcome(DonationStatus.PENDING_CLEARANCE, "CHK-" + checkNumber,
                "We will confirm your gift as soon as check #" + checkNumber + " clears, usually within 3-5 business days.");
    }
}
