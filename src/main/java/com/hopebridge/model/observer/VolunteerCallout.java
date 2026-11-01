package com.hopebridge.model.observer;

import com.hopebridge.model.entity.Event;
import com.hopebridge.model.entity.Role;
import com.hopebridge.model.service.NotificationService;
import com.hopebridge.model.support.Dates;

/**
 * OBSERVER - a Concrete Observer of the EventBoard.
 * When a new event is published, every volunteer gets a call for helping hands.
 */
public class VolunteerCallout implements Observer<EventBulletin> {

    private final NotificationService notificationService;

    public VolunteerCallout(Subject<EventBulletin> eventBoard, NotificationService notificationService) {
        this.notificationService = notificationService;
        eventBoard.registerObserver(this);
    }

    @Override
    public void update(EventBulletin bulletin) {
        if (bulletin.kind() == EventBulletin.Kind.PUBLISHED) {
            Event event = bulletin.event();
            notificationService.sendToRole(Role.VOLUNTEER, "New event needs helping hands: " + event.getTitle(),
                    event.getTitle() + " is happening on " + Dates.friendly(event.getStartsAt()) + " at "
                            + event.getLocation() + ". Register from the events page if you can lend a hand!");
        }
    }
}
