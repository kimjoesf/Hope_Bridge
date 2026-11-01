package com.hopebridge.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A block of hours a volunteer gave on a given day, optionally at one of the events.
 */
public class VolunteerShift {

    private int id;
    private int volunteerId;
    private Integer eventId;
    private String eventTitle;
    private LocalDate workDate;
    private BigDecimal hours;    // i think BigDecimal is too much it may be changed latter i think int is fine ? .
    private String note;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(int volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
