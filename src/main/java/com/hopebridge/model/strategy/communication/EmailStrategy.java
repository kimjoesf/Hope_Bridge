package com.hopebridge.model.strategy.communication;

import com.hopebridge.model.entity.Notification;
import com.hopebridge.model.entity.User;

/**
 * Concrete strategy: a proper letter with a greeting and a signature.
 * E-mail has room, so we keep the whole message.
 */
public class EmailStrategy implements CommunicationStrategy {

    @Override
    public String getCode() {
        return "EMAIL";
    }

    @Override
    public String getLabel() {
        return "Email";
    }

    @Override
    public String getDescription() {
        return "Full letters with every detail. Best for receipts and records.";
    }

    @Override
    public Notification deliver(User recipient, String subject, String body) {
        String letter = "Dear " + recipient.getFirstName() + ",\n\n"
                + body + "\n\n"
                + "With gratitude,\nThe HopeBridge team";
        return new Notification(recipient.getId(), getCode(), subject, letter);
    }
}
