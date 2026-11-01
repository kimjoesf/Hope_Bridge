package com.hopebridge.model.entity;

import com.hopebridge.model.strategy.communication.EmailStrategy;

/**
 * A member of the organization's team. Staff keep the lookup tables and events in shape,
 * and get internal alerts (like large gifts) by e-mail.
 */
public class Staff extends User {

    public Staff() {
        this.communicationStrategy = new EmailStrategy();
    }

    @Override
    public Role getRole() {
        return Role.STAFF;
    }
}
