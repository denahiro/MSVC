/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util.event;

import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author stko
 */
public class EventSource<EventType extends Event> {

    private final Collection<EventListener<EventType>> listeners = new HashSet<>();

    public synchronized EventSource<EventType> addListener(EventListener<EventType> listener) {
        listeners.add(listener);
        return this;
    }

    public synchronized EventSource<EventType> removeListener(EventListener<EventType> listener) {
        listeners.remove(listener);
        return this;
    }

    public final void fireEvent(EventType event) {
        callListeners(getListenerArray(), event);
    }

    protected synchronized EventListener<EventType>[] getListenerArray() {
        EventListener<EventType>[] toCall = new EventListener[1];
        return listeners.toArray(toCall);
    }

    protected void callListeners(EventListener<EventType>[] toCall, EventType event) {
        for (EventListener<EventType> listener : toCall) {
            listener.onEvent(event);
        }
    }
}
