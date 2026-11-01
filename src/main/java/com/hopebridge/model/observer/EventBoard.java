package com.hopebridge.model.observer;

/**
 * OBSERVER - a second Concrete Subject.
 *
 * "Notify interested parties when an event is updated." Every time staff publish,
 * change or send a reminder about an event, the board posts a bulletin and the
 * observers decide who should hear about it.
 */
public class EventBoard extends AbstractSubject<EventBulletin> {

    private EventBulletin latestBulletin;

    public synchronized void post(EventBulletin bulletin) {
        this.latestBulletin = bulletin;
        notifyObservers();
    }

    @Override
    protected EventBulletin getState() {
        return latestBulletin;
    }
}
