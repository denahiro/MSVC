/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util;

import ch.prometheus.msvc.util.event.EventSource;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author stko
 */
public class FiniteStreamConnector extends StreamConnector {

    private final int totalNumberOfBytes;
    private EventSource<ProgressEvent> progressEventSource;

    public FiniteStreamConnector(InputStream inStream, OutputStream outStream, int totalNumberOfBytes) {
        super(inStream, outStream);
        this.totalNumberOfBytes = totalNumberOfBytes;
    }

    public FiniteStreamConnector setProgressEventSource(EventSource<ProgressEvent> source) {
        progressEventSource = source;
        return this;
    }

    @Override
    protected void fireEvent(int totalBytesRead) {
        super.fireEvent(totalBytesRead);
        if (progressEventSource != null) {
            progressEventSource.fireEvent(new ProgressEvent(this, totalBytesRead, totalNumberOfBytes));
        }
    }
}
