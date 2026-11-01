package com.hopebridge.model.strategy.donation;

import com.hopebridge.model.decorator.Contribution;
import com.hopebridge.model.decorator.InKindContribution;
import com.hopebridge.model.dto.DonationRequest;
import com.hopebridge.model.entity.DonationStatus;
import com.hopebridge.model.support.Codes;
import com.hopebridge.model.support.Rules;

/**
 * Concrete strategy: goods instead of money (food, clothes, school supplies...).
 * We need to know what the items are and roughly what they are worth for the
 * tax receipt, and then we wait for the donor to drop them off.
 */
public class InKindMethod implements DonationMethod {

    @Override
    public String getCode() {
        return "IN_KIND";
    }

    @Override
    public String getLabel() {
        return "In-kind";
    }

    @Override
    public String getDescription() {
        return "Donate goods like food, clothing or supplies. Drop them at our center.";
    }

    @Override
    public Contribution baseContribution(DonationRequest request) {
        return new InKindContribution(request.itemDescription().trim(), request.estimatedValue());
    }

    @Override
    public DonationOutcome process(DonationRequest request) {
        Rules.requireText(request.itemDescription(), "Item description", 255);
        Rules.requireAmount(request.estimatedValue(), "Estimated value");
        Rules.require(request.addOns().isEmpty(), "Gift add-ons only apply to monetary donations.");
        return new DonationOutcome(DonationStatus.AWAITING_DROP_OFF, Codes.random("INK-", 8),
                "Please bring your items to our community center. Show this reference at the desk.");
    }
}
