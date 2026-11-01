package com.hopebridge.model.observer;

/**
 * OBSERVER - the Subject interface.
 * Objects use this interface to register as observers and also to remove themselves.
 *
 * T is the piece of state the subject pushes to its observers when it changes
 * (the lecture pushed temperature, humidity and pressure; we push a Donation or an EventBulletin).
 */
public interface Subject<T> {

    void registerObserver(Observer<T> observer);

    void removeObserver(Observer<T> observer);

    void notifyObservers();
}
