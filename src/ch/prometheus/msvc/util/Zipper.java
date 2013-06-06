/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author stko
 */
public class Zipper extends CloseableTask {

    private final ZipOutputStream outStream;
    private final Collection<File> toZip;
    private final File basePath;

    public Zipper(File zipFile, Collection<File> toZip, File basePath) throws FileNotFoundException {
        this.outStream = new ZipOutputStream(new FileOutputStream(zipFile));
        this.toZip = toZip;
        this.basePath = basePath;
    }

    public Zipper(File zipFile, Collection<File> toZip) throws FileNotFoundException {
        this(zipFile, toZip, new File("."));
    }

    @Override
    public Boolean call() throws IOException {
        processFiles(toZip);
        return true;
    }

    @Override
    public void close() throws IOException {
        outStream.close();
    }

    private void processFiles(Collection<File> files) throws IOException {
        for (File currentFile : files) {
            if (currentFile.isDirectory()) {
                processFiles(Arrays.asList(currentFile.listFiles()));
            } else {
                zipFile(currentFile);
            }
        }
    }

    private void zipFile(File file) throws IOException {
        ZipEntry entry = new ZipEntry(basePath.toURI().relativize(file.toURI()).getPath());
        try (FileInputStream inStream = new FileInputStream(file)) {
            outStream.putNextEntry(entry);
            StreamConnector connector = new FiniteStreamConnector(inStream, outStream, (int) file.length());
            connector.call();
        } finally {
            outStream.closeEntry();
        }
    }
}
