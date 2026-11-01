package com.hopebridge.model.entity;

import java.time.LocalDateTime;

/**
 * A user's seat at an event, identified by a ticket code they can show at the door.
 */
public class EventRegistration {

    private int id;
    private int userId;
    private String ticketCode;
    private LocalDateTime registeredAt;
    private Event event;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    // UNUSED
    // public LocalDateTime getRegisteredAt() {
    //     return registeredAt;
    // }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
