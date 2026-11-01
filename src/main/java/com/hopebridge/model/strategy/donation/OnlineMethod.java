package com.hopebridge.model.strategy.donation;

import com.hopebridge.model.decorator.Contribution;
import com.hopebridge.model.decorator.MoneyContribution;
import com.hopebridge.model.dto.DonationRequest;
import com.hopebridge.model.entity.DonationStatus;
import com.hopebridge.model.support.Codes;
import com.hopebridge.model.support.Rules;

/**
 * Concrete strategy: an online card gift. It clears instantly, so it is confirmed right away.
 * (Phase 2's Payment Module plugs a real gateway in behind this class.)
 */
public class OnlineMethod implements DonationMethod {

    @Override
    public String getCode() {
        return "ONLINE";
    }

    @Override
    public String getLabel() {
        return "Online";
    }

    @Override
    public String getDescription() {
        return "Give by card in seconds. Confirmed instantly.";
    }

    @Override
    public Contribution baseContribution(DonationRequest request) {
        return new MoneyContribution(request.amount());
    }

    @Override
    public DonationOutcome process(DonationRequest request) {
        Rules.requireAmount(request.amount(), "Gift amount");
        return new DonationOutcome(DonationStatus.CONFIRMED, Codes.random("TXN-", 10),
                "Your online gift went through instantly.");
    }
}
