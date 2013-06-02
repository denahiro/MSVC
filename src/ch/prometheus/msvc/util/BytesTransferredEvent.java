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
public class BytesTransferredEvent extends Event<StreamConnector> {

    private final int numberOfBytesTransferred;

    public BytesTransferredEvent(StreamConnector sender, int numberOfBytesTransferred) {
        super(sender);
        this.numberOfBytesTransferred = numberOfBytesTransferred;
    }

    public int getNumberOfBytesTransferred() {
        return numberOfBytesTransferred;
    }
}
