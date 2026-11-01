package com.hopebridge.model.observer;

/**
 * OBSERVER - the Observer interface.
 * It has just one method, update(), that gets called when the Subject's state changes.
 */
public interface Observer<T> {

    void update(T state);

}