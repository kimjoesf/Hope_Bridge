package com.hopebridge.model.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DECORATOR - a Concrete Component:a plain monetary gift.
 */
public class MoneyContribution extends Contribution {

    private final BigDecimal amount;

    public MoneyContribution(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        description = "Monetary gift";
    }

    @Override
    public BigDecimal amount() {
        return amount;
    }

    @Override
    public BigDecimal taxDeductibleAmount() {
        return amount;
    }
}
