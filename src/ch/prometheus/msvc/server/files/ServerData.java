/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;

/**
 *
 * @author Denahiro
 */
public class ServerData {

    private static final String DEFAULT_PROPERTY_RESOURCE = "server.properties";
    private final Collection<File> loadedFiles = new HashSet<>();
    private Properties properties;
    private String name;

    public ServerData() {
    }

    public void load(File localDirectory, File remoteDirectory) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void save(File localDirectory, File remoteDirectory) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void create(File localDirectory) throws IOException {
        loadDefaultProperties();
        createName(localDirectory);
        saveProperties(localDirectory);
        createServerDirectory(localDirectory);
    }

    public void delete() {
        for (File f : loadedFiles) {
            deleteRecursion(f);
        }
        loadedFiles.clear();
    }

    public String getName() {
        return name;
    }

    private void deleteRecursion(File f) {
        if (f.isDirectory()) {
            File[] next = f.listFiles();
            for (File n : next) {
                deleteRecursion(n);
            }
        }
        f.delete();
    }

    private void createName(File localDirectory) {
        name = properties.getProperty("level-name");
        String serverPostFix = "";
        int postFix = 0;
        while (new File(localDirectory, name + serverPostFix).exists()) {
            ++postFix;
            serverPostFix = Integer.toString(postFix);
        }
        name = name + serverPostFix;
        properties.setProperty("level-name", name);
    }

    private void loadDefaultProperties() throws IOException {
        try (InputStream inStream = this.getClass().getResourceAsStream(DEFAULT_PROPERTY_RESOURCE)) {
            properties = new Properties();
            properties.load(inStream);
        }
    }

    private void saveProperties(File localDirectory) throws IOException {
        File propertyFile = new File(localDirectory, DEFAULT_PROPERTY_RESOURCE);
        propertyFile.getParentFile().mkdirs();
        try (OutputStream outStream = new FileOutputStream(propertyFile)) {
            properties.store(outStream, "Written by MSVC at " + new Date().toString());
        }
        loadedFiles.add(propertyFile);
    }

    private void createServerDirectory(File localDirectory) {
        File serverDirectory = new File(localDirectory, name);
        serverDirectory.mkdirs();
        loadedFiles.add(serverDirectory);
    }
}
