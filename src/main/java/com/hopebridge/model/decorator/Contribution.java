package com.hopebridge.model.decorator;

import java.math.BigDecimal;

/**
 * DECORATOR PATTERN.
 *
 * A contribution is what a donor gives. On top of it, a donor may choose extras:
 * cover the processing fees, have their employer match the gift, dedicate it to
 * someone... Any combination is allowed.
 */

public abstract class Contribution {
    
    protected String description = "Unknown contribution";

    public String getDescription() {
        return description;
    }

    // Like cost() on Beverage: the full value this gift delivers to the cause.
    public abstract BigDecimal amount();

    // The part the donor personally paid, this amount is to be added to the receipt so the donor can claim tax deduction.
    public abstract BigDecimal taxDeductibleAmount();

}
