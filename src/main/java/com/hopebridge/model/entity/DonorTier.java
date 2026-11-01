package com.hopebridge.model.entity;

import java.math.BigDecimal;

/**
 * Client requirement: donor recognition tiers.
 * the tier increases or upgrades as the donor contributes in more cases.
 */
public enum DonorTier {

    SUPPORTER("Supporter", 0),
    BRONZE("Bronze", 100),
    SILVER("Silver", 500),
    GOLD("Gold", 2_000),
    PLATINUM("Platinum", 10_000);

    private final String label;
    private final BigDecimal threshold;

    DonorTier(String label, int threshold) {
        this.label = label;
        this.threshold = BigDecimal.valueOf(threshold);
    }

    // Walk the ladder from the top down and stop at the first rung the donor has reached.
    public static DonorTier forLifetimeGiving(BigDecimal lifetimeTotal) {
        DonorTier[] tiers = values();
        for (int i = tiers.length - 1; i >= 0; i--) {
            if (lifetimeTotal.compareTo(tiers[i].threshold) >= 0) {
                return tiers[i];
            }
        }
        return SUPPORTER;
    }


    // The next rung up, or null when the donor is already at the top.
    public DonorTier getNext() {
        return ordinal() + 1 < values().length ? values()[ordinal() + 1] : null;
    }

    
    public boolean isHigherThan(DonorTier other) {
        return compareTo(other) > 0;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getThreshold() {
        return threshold;
    }
}
