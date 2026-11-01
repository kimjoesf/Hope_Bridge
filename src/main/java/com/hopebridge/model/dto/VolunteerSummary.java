package com.hopebridge.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A volunteer's totals: what we print on their dashboard and on their certificate.
 */
public class VolunteerSummary {

    private final BigDecimal totalHours;
    private final int shiftCount;
    private final LocalDate firstDay;
    private final LocalDate lastDay;

    public VolunteerSummary(BigDecimal totalHours, int shiftCount, LocalDate firstDay, LocalDate lastDay) {
        this.totalHours = totalHours;
        this.shiftCount = shiftCount;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
    }

    public boolean hasHours() {
        return shiftCount > 0;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public int getShiftCount() {
        return shiftCount;
    }

    public LocalDate getFirstDay() {
        return firstDay;
    }

    public LocalDate getLastDay() {
        return lastDay;
    }
}
