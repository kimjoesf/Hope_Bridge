package com.hopebridge.model.decorator;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DECORATOR - Concrete Component: goods, valued at their estimated worth.
 */
public class InKindContribution extends Contribution {

    private final BigDecimal estimatedValue;

    public InKindContribution(String items, BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue.setScale(2, RoundingMode.HALF_UP);
        description = "In-kind gift: " + items;
    }

    @Override
    public BigDecimal amount() {
        return estimatedValue;
    }

    @Override
    public BigDecimal taxDeductibleAmount() {
        return estimatedValue;
    }
}
