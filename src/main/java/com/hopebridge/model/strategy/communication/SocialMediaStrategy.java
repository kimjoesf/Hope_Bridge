package com.hopebridge.model.strategy.communication;

import com.hopebridge.model.entity.Notification;
import com.hopebridge.model.entity.User;

/**
 * Concrete strategy: a friendly direct message with a mention and our hashtag,
 * kept under the usual 280-character post limit.
 */
public class SocialMediaStrategy implements CommunicationStrategy {

    private static final int POST_LIMIT = 280;
    private static final String HASHTAG = " #HopeBridge";

    @Override
    public String getCode() {
        return "SOCIAL";
    }

    @Override
    public String getLabel() {
        return "Social media";
    }

    @Override
    public String getDescription() {
        return "Direct messages on our social channels, with a little extra cheer.";
    }

    @Override
    public Notification deliver(User recipient, String subject, String body) {
        String mention = "@" + recipient.getFullName().toLowerCase().replaceAll("[^a-z0-9]", "");
        String post = MessageText.shorten(mention + " " + MessageText.singleLine(body), POST_LIMIT - HASHTAG.length()) + HASHTAG;
        return new Notification(recipient.getId(), getCode(), subject, post);
    }
}
