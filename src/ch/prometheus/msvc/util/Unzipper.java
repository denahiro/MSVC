/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author stko
 */
public class Unzipper extends CloseableTask {

    private final ZipFile zipFile;
    private final File destination;

    public Unzipper(File zipFile, File destination) throws IOException {
        this.zipFile = new ZipFile(zipFile);
        this.destination = destination;
    }

    @Override
    public Boolean call() throws IOException {
        Enumeration entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            File outputFile = new File(destination, entry.getName());
            outputFile.getParentFile().mkdirs();
            try (InputStream inStream = zipFile.getInputStream(entry);
                 OutputStream outStream = new FileOutputStream(outputFile)) {
                new FiniteStreamConnector(inStream, outStream, (int) entry.getSize()).call();
            }
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
