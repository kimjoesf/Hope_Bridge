package com.hopebridge.model.observer;

import com.hopebridge.model.dao.RegistrationDao;
import com.hopebridge.model.entity.Event;
import com.hopebridge.model.service.NotificationService;
import com.hopebridge.model.support.Dates;

/**
 * OBSERVER - a Concrete Observer of the EventBoard.
 * "Send event reminders and communications to attendees": when an event changes or a
 * reminder goes out, everyone registered hears about it on their own channel.
 */
public class AttendeeNotifier implements Observer<EventBulletin> {

    private final RegistrationDao registrationDao;
    private final NotificationService notificationService;

    public AttendeeNotifier(Subject<EventBulletin> eventBoard, RegistrationDao registrationDao,
                            NotificationService notificationService) {
        this.registrationDao = registrationDao;
        this.notificationService = notificationService;
        eventBoard.registerObserver(this);
    }

    @Override
    public void update(EventBulletin bulletin) {
        Event event = bulletin.event();
        String when = Dates.friendly(event.getStartsAt()) + " at " + event.getLocation();
        switch (bulletin.kind()) {
            case UPDATED -> notifyAttendees(event, "Event update: " + event.getTitle(),
                    "Heads up! The details of " + event.getTitle() + " have changed. It now takes place on " + when + ".");
            case REMINDER -> notifyAttendees(event, "Reminder: " + event.getTitle(),
                    "Just a friendly reminder that " + event.getTitle() + " is coming up on " + when + ". We can't wait to see you!");
            default -> {
                // A freshly published event has no attendees yet, so there is nobody to tell.
            }
        }
    }

    private void notifyAttendees(Event event, String subject, String body) {
        registrationDao.findAttendeeIds(event.getId()).forEach(userId -> notificationService.send(userId, subject, body));
    }
}
