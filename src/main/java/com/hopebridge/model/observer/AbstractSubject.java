package com.hopebridge.model.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER - the observer-list bookkeeping that every concrete subject needs.
 *
 * WeatherData in the lecture kept an ArrayList of observers and implemented
 * register / remove / notify itself. We have two subjects (donations and events),
 * so that same code lives here once and both of them extend it.
 */
public abstract class AbstractSubject<T> implements Subject<T> {

    private final List<Observer<T>> observers = new ArrayList<>();

    // When an observer registers, we just add it to the end of the list.
    @Override
    public synchronized void registerObserver(Observer<T> observer) {
        observers.add(observer);
    }

    // Likewise, when an observer wants to un-register, we just take it off the list.
    @Override
    public synchronized void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    // This is where we tell all the observers about the new state.
    // Because they are all Observers, we know they all implement update().
    @Override
    public synchronized void notifyObservers() {
        T state = getState();
        for (Observer<T> observer : observers) {
            observer.update(state);
        }
    }

    // Each concrete subject decides what "its state" is.
    protected abstract T getState();
}
