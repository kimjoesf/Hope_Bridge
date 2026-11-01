package com.hopebridge.model.entity;

import com.hopebridge.model.strategy.communication.SmsStrategy;

/**
 * A person who gives time. Volunteers are usually on the move between shifts,
 * so a short SMS is the default way to reach them.
 */
public class Volunteer extends User {

    public Volunteer() {
        this.communicationStrategy = new SmsStrategy();
    }

    @Override
    public Role getRole() {
        return Role.VOLUNTEER;
    }
}
