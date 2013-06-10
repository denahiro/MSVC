/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author Denahiro
 */
public class PropertyHandler {

    private boolean dataDirty = false;
    private final Properties loadedProperties;

    public PropertyHandler(InputStream inStream) throws IOException {
        this.loadedProperties = initProperties(inStream);
    }

    public String getProperty(String key) {
        return this.loadedProperties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        this.dataDirty = true;
        this.loadedProperties.setProperty(key, value);
    }

    public boolean isDirty() {
        return dataDirty;
    }

    public void savePropertyChanges(OutputStream outStream) throws IOException {
        this.loadedProperties.store(outStream, "Minecraft server properties written by MSVC at " + new Date().toString());
        this.dataDirty = false;
    }

    private Properties initProperties(InputStream inStream) throws IOException {
        Properties output = new Properties();
        output.load(inStream);
        return output;
    }
}
