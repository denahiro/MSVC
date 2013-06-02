/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util.event;

/**
 *
 * @author stko
 */
public class Event<SenderType> {

    private final SenderType sender;

    public Event(SenderType sender) {
        this.sender = sender;
    }

    public SenderType getSender() {
        return sender;
    }
}
