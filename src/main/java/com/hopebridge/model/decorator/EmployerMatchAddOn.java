package com.hopebridge.model.decorator;

import java.math.BigDecimal;

/**
 * DECORATOR - a Concrete Decorator.
 * The donor's employer matches the gift one-to-one, doubling its impact.
 */
public class EmployerMatchAddOn extends ContributionAddOn {

    public static final BigDecimal MATCH_MULTIPLIER = BigDecimal.valueOf(2);

    private final String employer;

    public EmployerMatchAddOn(Contribution contribution, String employer) {
        super(contribution);
        this.employer = employer;
    }

    @Override
    public String getDescription() {
        return contribution.getDescription() + ", matched by " + employer;
    }

    @Override
    public BigDecimal amount() {
        return contribution.amount().multiply(MATCH_MULTIPLIER);
    }

    // The matched half is the employer's money, not the donor's, so the deductible part stays the same because user is the one that receives the receipt.
    @Override
    public BigDecimal taxDeductibleAmount() {
        return contribution.taxDeductibleAmount();
    }
}
