package com.hopebridge.model.service;

import com.hopebridge.model.dao.EventDao;
import com.hopebridge.model.dao.RegistrationDao;
import com.hopebridge.model.dto.EventForm;
import com.hopebridge.model.entity.Event;
import com.hopebridge.model.entity.EventRegistration;
import com.hopebridge.model.entity.LookupTable;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.exception.NotFoundException;
import com.hopebridge.model.observer.EventBoard;
import com.hopebridge.model.observer.EventBulletin;
import com.hopebridge.model.support.Codes;
import com.hopebridge.model.support.Dates;
import com.hopebridge.model.support.Rules;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event management: publishing events, registering people and keeping attendees informed.
 */
public class EventService {

    private static final int MAX_CAPACITY = 100_000;

    private final EventDao eventDao;
    private final RegistrationDao registrationDao;
    private final LookupService lookupService;
    private final NotificationService notificationService;
    private final EventBoard eventBoard;

    public EventService(EventDao eventDao, RegistrationDao registrationDao, LookupService lookupService,
                        NotificationService notificationService, EventBoard eventBoard) {
        this.eventDao = eventDao;
        this.registrationDao = registrationDao;
        this.lookupService = lookupService;
        this.notificationService = notificationService;
        this.eventBoard = eventBoard;
    }

    public List<Event> upcoming() {
        return eventDao.findUpcoming();
    }

    public List<Event> all() {
        return eventDao.findAll();
    }

    public Event find(int id) {
        return eventDao.findById(id).orElseThrow(() -> new NotFoundException("Event #" + id + " does not exist."));
    }

    public String register(int eventId, User user) {
        Event event = find(eventId);
        // is past means you can't register in an even that has started already .
        Rules.require(!event.isPast(), "This event has already taken place.");
        Rules.require(!registrationDao.exists(eventId, user.getId()), "You are already registered for this event.");
        Rules.require(!event.isFull(), "Sorry, this event is fully booked.");

        String ticketCode = Codes.random("EVT-", 8);
        registrationDao.insert(eventId, user.getId(), ticketCode);
        notificationService.send(user, "You're registered: " + event.getTitle(),
                "Your ticket code is " + ticketCode + ". See you on " + Dates.friendly(event.getStartsAt())
                        + " at " + event.getLocation() + ".");
        return ticketCode;
    }
    // retutns all the events a user is registered for .
    public Event unregister(int eventId, User user) {
        Event event = find(eventId);
        Rules.require(!event.isPast(), "This event has already taken place, so the registration stays on record.");
        Rules.require(registrationDao.exists(eventId, user.getId()), "You are not registered for this event.");

        registrationDao.delete(eventId, user.getId());
        notificationService.send(user, "Registration cancelled: " + event.getTitle(),
                "Your seat for " + event.getTitle() + " on " + Dates.friendly(event.getStartsAt())
                        + " has been released. We hope to see you at another event soon.");
        return event;
    }

    public List<EventRegistration> registrationsFor(int userId) {
        return registrationDao.findByUser(userId);
    }
    // returns all the event ids a user is registered for .
    public List<Integer> registeredEventIds(int userId) {
        return registrationDao.findEventIdsForUser(userId);
    }

    public Event save(EventForm form, User staff) {
        Event event = form.isNew() ? new Event() : find(form.id());
        event.setTitle(Rules.requireText(form.title(), "Title", 140));
        event.setSummary(Rules.requireText(form.summary(), "Summary", 600));
        event.setLocation(Rules.requireText(form.location(), "Location", 160));
        lookupService.requireActive(LookupTable.EVENT_CATEGORIES, form.categoryId(), "Please choose an event category.");
        event.setCategoryId(form.categoryId());

        Rules.require(form.startsAt() != null, "Please choose when the event starts.");
        Rules.require(form.startsAt().isAfter(LocalDateTime.now()), "The start time must be in the future.");
        event.setStartsAt(form.startsAt());

        Rules.require(form.capacity() != null && form.capacity() > 0 && form.capacity() <= MAX_CAPACITY,"Capacity must be between 1 and " + MAX_CAPACITY + ".");
        Rules.require(form.capacity() >= event.getRegisteredCount(),"Capacity cannot be lower than the " + event.getRegisteredCount() + " people already registered.");
        event.setCapacity(form.capacity());

        if (form.isNew()) {
            event.setCreatedBy(staff.getId());
            eventDao.insert(event);
            eventBoard.post(new EventBulletin(event, EventBulletin.Kind.PUBLISHED));
        } else {
            eventDao.update(event);
            eventBoard.post(new EventBulletin(event, EventBulletin.Kind.UPDATED));
        }
        return event;
    }

    public Event sendReminder(int eventId) {
        Event event = find(eventId);
        Rules.require(!event.isPast(), "This event is over, so there is nothing to remind people about.");
        Rules.require(event.getRegisteredCount() > 0, "Nobody has registered for this event yet.");
        eventBoard.post(new EventBulletin(event, EventBulletin.Kind.REMINDER));
        return event;
    }
}

/*
 sendReminder: simply sends a bulletin (a message or a notify ) what happens is that when a staff member make any modifications to an event the EventBoard (an observer) takes it and publishes it to all the interested parts . 
 */