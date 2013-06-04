/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util;

import ch.prometheus.msvc.util.event.EventSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 *
 * @author stko
 */
public class StreamConnector implements Callable<Boolean> {

    public final static int DEFAULT_TRANSFER_CHUNK_SIZE = 1000;
    private int transferSize;
    private final InputStream inStream;
    private final OutputStream outStream;
    private EventSource<BytesTransferredEvent> bytesTransferredEventSource;

    public StreamConnector(InputStream inStream, OutputStream outStream) {
        this.inStream = inStream;
        this.outStream = outStream;
        transferSize = DEFAULT_TRANSFER_CHUNK_SIZE;
    }

    public StreamConnector setBytesTransferredEventSource(EventSource<BytesTransferredEvent> source) {
        bytesTransferredEventSource = source;
        return this;
    }

    public StreamConnector setTransferSize(int size) {
        transferSize = size;
        return this;
    }

    @Override
    public final Boolean call() throws IOException {
        transferBytes();
        return true;
    }

    protected void fireEvent(int totalBytesRead) {
        if (bytesTransferredEventSource != null) {
            bytesTransferredEventSource.fireEvent(new BytesTransferredEvent(this, totalBytesRead));
        }
    }

    protected int getBytes(byte[] carrier) throws IOException {
        return inStream.read(carrier);
    }

    protected void putBytes(byte[] carrier) throws IOException {
        outStream.write(carrier);
    }

    private void transferBytes() throws IOException {
        byte[] carrier = new byte[transferSize];
        int currentBytesRead;
        int totalBytesRead = 0;
        while ((currentBytesRead = getBytes(carrier)) > 0) {
            totalBytesRead += currentBytesRead;
            if (currentBytesRead < carrier.length) {
                sendBytes(Arrays.copyOf(carrier, currentBytesRead), totalBytesRead);
            } else {
                sendBytes(carrier, totalBytesRead);
            }
        }
    }

    private void sendBytes(byte[] carrier, int totalBytesRead) throws IOException {
        putBytes(carrier);
        fireEvent(totalBytesRead);
    }
}
