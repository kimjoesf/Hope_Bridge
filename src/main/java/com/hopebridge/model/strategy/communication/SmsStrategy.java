package com.hopebridge.model.strategy.communication;

import com.hopebridge.model.entity.Notification;
import com.hopebridge.model.entity.User;

/**
 * Concrete strategy: one short text. An SMS is 160 characters, so we squeeze
 * the subject and body into that and cut the rest.
 */
public class SmsStrategy implements CommunicationStrategy {

    private static final int SMS_LIMIT = 160;

    @Override
    public String getCode() {
        return "SMS";
    }

    @Override
    public String getLabel() {
        return "SMS";
    }

    @Override
    public String getDescription() {
        return "Short texts to your phone. Great for reminders on the go.";
    }

    @Override
    public Notification deliver(User recipient, String subject, String body) {
        String text = MessageText.shorten("HopeBridge: " + subject + " - " + MessageText.singleLine(body), SMS_LIMIT);
        return new Notification(recipient.getId(), getCode(), subject, text);
    }
}
