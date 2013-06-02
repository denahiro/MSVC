/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util.event;

/**
 *
 * @author stko
 */
public interface EventListener<EventType extends Event> {

    void onEvent(EventType event);
}
