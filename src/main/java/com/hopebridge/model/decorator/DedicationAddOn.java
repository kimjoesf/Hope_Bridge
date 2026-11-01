package com.hopebridge.model.decorator;

import java.math.BigDecimal;

/**
 * DECORATOR - a Concrete Decorator.
 * The gift is made in honour of someone. It changes the description only .
 */
public class DedicationAddOn extends ContributionAddOn {

    private final String honoree;

    public DedicationAddOn(Contribution contribution, String honoree) {
        super(contribution);
        this.honoree = honoree;
    }

    @Override
    public String getDescription() {
        return contribution.getDescription() + ", in honour of " + honoree;
    }

    @Override
    public BigDecimal amount() {
        return contribution.amount();
    }

    @Override
    public BigDecimal taxDeductibleAmount() {
        return contribution.taxDeductibleAmount();
    }
}