package com.hopebridge.model.observer;

import com.hopebridge.model.entity.Event;

/**
 * The state the EventBoard pushes to its observers: which event, and what happened to it.
 */
public record EventBulletin(Event event, Kind kind) {

    public enum Kind {
        PUBLISHED,
        UPDATED,
        REMINDER
    }
}
