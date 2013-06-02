/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util;

import ch.prometheus.msvc.util.event.Event;

/**
 *
 * @author stko
 */
public class ProgressEvent extends Event<FiniteStreamConnector> {

    private final int current;
    private final int total;

    public ProgressEvent(FiniteStreamConnector sender, int current, int total) {
        super(sender);
        this.current = current;
        this.total = total;
    }

    public double getPercentage() {
        return current / ((double) total);
    }

    public int getCurrent() {
        return current;
    }

    public int getTotal() {
        return total;
    }
}
