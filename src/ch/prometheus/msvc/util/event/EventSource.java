/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author stko
 */
public class EventSource<EventType extends Event> {

    private final Collection<EventListener<EventType>> listeners = new HashSet<>();
    private final ReentrantLock listenerLock = new ReentrantLock();
    private final ReentrantLock eventLock = new ReentrantLock();

    public EventSource<EventType> addListener(EventListener<EventType> listener) {
        listenerLock.lock();
        try {
            listeners.add(listener);
            return this;
        } finally {
            listenerLock.unlock();
        }
    }

    public EventSource<EventType> removeListener(EventListener<EventType> listener) {
        listenerLock.lock();
        try {
            listeners.remove(listener);
            return this;
        } finally {
            listenerLock.unlock();
        }
    }

    public final void fireEvent(EventType event) {
        callListeners(copyListeners(), event);
    }

    private Collection<EventListener<EventType>> copyListeners() {
        listenerLock.lock();
        try {
            Collection<EventListener<EventType>> copy = new ArrayList<>();
            for (EventListener<EventType> listener : listeners) {
                copy.add(listener);
            }
            return copy;
        } finally {
            listenerLock.unlock();
        }
    }

    private void callListeners(Collection<EventListener<EventType>> toCall, EventType event) {
        eventLock.lock();
        try {
            for (EventListener<EventType> listener : toCall) {
                listener.onEvent(event);
            }
        } finally {
            eventLock.unlock();
        }
    }
}
