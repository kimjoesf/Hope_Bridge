package com.hopebridge.model.strategy.communication;

import com.hopebridge.model.entity.Notification;
import com.hopebridge.model.entity.User;

/**
 * STRATEGY - the Strategy interface (like FlyBehavior in the lab).
 *
 * One family of algorithms: "how do we shape and send a message to someone?"
 * Each channel is its own class, so they are interchangeable at runtime and a new
 * channel never touches the User class.
 *
 * Real gateways (SMTP, an SMS provider, social APIs) arrive in Phase 2 through the
 * Adapter pattern. For now every strategy formats the message for its channel and
 * hands back a Notification, which we keep in the user's outbox.
 */
public interface CommunicationStrategy {

    String getCode();

    String getLabel();

    String getDescription();

    Notification deliver(User recipient, String subject, String body);
}
