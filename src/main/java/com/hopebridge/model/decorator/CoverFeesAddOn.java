package com.hopebridge.model.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DECORATOR - a Concrete Decorator (like Mocha).
 * The donor adds 3% so card fees do not eat into the gift.
 */
public class CoverFeesAddOn extends ContributionAddOn {
    
    public static final BigDecimal FEE_RATE = new BigDecimal("0.03");  // i could make it double to be easier but BigDecimal is more precise to avoid rounding errors or strange numbers . 

    public CoverFeesAddOn(Contribution contribution) {
        super(contribution);
    }

    @Override
    public String getDescription() {
        return contribution.getDescription() + ", processing fees covered";
    }

    // The extra 3% goes to the card processor, not to us, so what the organization receives stays the same.
    @Override
    public BigDecimal amount() {
        return contribution.amount();
    }

    // But the donor did pay the fee out of their own pocket, so it counts on the tax receipt - just like Mocha adds 0.20 to the beverage's cost.
    @Override
    public BigDecimal taxDeductibleAmount() {
        return contribution.taxDeductibleAmount().add(fee());
    }

    private BigDecimal fee() {
        return contribution.amount().multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
