package com.hopebridge.model.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Client requirement: a fundraising campaign with a goal, so donors can see
 * how close we are and what their gift moves forward.
 */
public class Campaign {

    private int id;
    private String title;
    private String summary;
    private BigDecimal goalAmount;
    private BigDecimal raisedAmount;
    private int categoryId;
    private String categoryName;
    private LocalDate endsOn;
    private boolean active;

    // Capped at 100 so an over-funded campaign still draws a full (not overflowing) progress bar.
    public int getProgressPercent() {
        if (goalAmount == null || goalAmount.signum() == 0) {
            return 0;
        }
        int percent = raisedAmount.multiply(BigDecimal.valueOf(100)).divide(goalAmount, 0, RoundingMode.DOWN).intValue();
        return Math.min(percent, 100);
    }

    // UNUSED
    // public BigDecimal getRemainingAmount() {
    //     return goalAmount.subtract(raisedAmount).max(BigDecimal.ZERO);
    // }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public BigDecimal getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(BigDecimal goalAmount) {
        this.goalAmount = goalAmount;
    }

    public BigDecimal getRaisedAmount() {
        return raisedAmount;
    }

    public void setRaisedAmount(BigDecimal raisedAmount) {
        this.raisedAmount = raisedAmount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // UNUSED
    // public LocalDate getEndsOn() {
    //     return endsOn;
    // }

    public void setEndsOn(LocalDate endsOn) {
        this.endsOn = endsOn;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
