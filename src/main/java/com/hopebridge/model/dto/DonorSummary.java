package com.hopebridge.model.dto;

import com.hopebridge.model.entity.DonorTier;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A donor's giving at a glance, including how far they are from the next tier.
 */
public class DonorSummary {

    private final BigDecimal lifetimeGiving;
    private final int donationCount;
    private final DonorTier tier;

    public DonorSummary(BigDecimal lifetimeGiving, int donationCount, DonorTier tier) {
        this.lifetimeGiving = lifetimeGiving;
        this.donationCount = donationCount;
        this.tier = tier;
    }

    public DonorTier getNextTier() {
        return tier.getNext();
    }

    public BigDecimal getAmountToNextTier() {
        DonorTier next = getNextTier();
        return next == null ? BigDecimal.ZERO : next.getThreshold().subtract(lifetimeGiving).max(BigDecimal.ZERO);
    }

    // How much of the way from the current tier to the next one the donor has covered.
    public int getProgressToNextTier() {
        DonorTier next = getNextTier();
        if (next == null) {
            return 100;
        }
        BigDecimal span = next.getThreshold().subtract(tier.getThreshold());
        BigDecimal covered = lifetimeGiving.subtract(tier.getThreshold());
        return covered.multiply(BigDecimal.valueOf(100)).divide(span, 0, RoundingMode.DOWN).intValue();
    }

    public BigDecimal getLifetimeGiving() {
        return lifetimeGiving;
    }

    public int getDonationCount() {
        return donationCount;
    }

    public DonorTier getTier() {
        return tier;
    }
}
