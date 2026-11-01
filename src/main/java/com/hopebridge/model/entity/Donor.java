package com.hopebridge.model.entity;

import com.hopebridge.model.strategy.communication.EmailStrategy;

/**
 * A person who gives. Donors expect proper receipts and thank-you letters,
 * so by default we reach them by e-mail.
 */
public class Donor extends User {

    public Donor() {
        this.communicationStrategy = new EmailStrategy();
    }

    @Override
    public Role getRole() {
        return Role.DONOR;
    }
}
