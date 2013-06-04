/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util.event;

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
        callListeners(getListenerArray(), event);
    }

    protected EventListener<EventType>[] getListenerArray() {
        listenerLock.lock();
        try {
            EventListener<EventType>[] toCall = new EventListener[listeners.size()];
            return listeners.toArray(toCall);
        } finally {
            listenerLock.unlock();
        }
    }

    protected void callListeners(EventListener<EventType>[] toCall, EventType event) {
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
