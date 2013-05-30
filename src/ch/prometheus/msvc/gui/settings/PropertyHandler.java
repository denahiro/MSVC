/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui.settings;

import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Denahiro
 */
public class PropertyHandler {

    private final File propertyFile;
    private boolean isDirty = false;
    private final Properties loadedProperties;

    public PropertyHandler(File propertyFile) {
        this.propertyFile = propertyFile;
        this.loadedProperties = initProperties();
    }

    public String getProperty(String key) {
        return this.loadedProperties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        this.isDirty = true;
        this.loadedProperties.setProperty(key, value);
    }

    public boolean onClosing(Component closing) {
        if (isDirty) {
            int closingResponse = JOptionPane.showConfirmDialog(closing, "Would you like to save the modified settings?");
            switch (closingResponse) {
                case 0:
                    savePropertyChanges();
                    break;
                case 1:
                    break;
                case 2:
                    return false;
            }
        }
        return true;
    }

    public void unflagPropertyChanges() {
        isDirty = false;
    }

    public void savePropertyChanges() {
        try (FileWriter outputWriter = new FileWriter(propertyFile)) {
            this.loadedProperties.store(outputWriter, "Minecraft server properties");
            this.isDirty = false;
        } catch (IOException ie) {
            System.out.println(ie);
        }
    }

    private Properties initProperties() {
        Properties output = new Properties();
        if (propertyFile.canRead()) {
            try (FileReader propertyReader = new FileReader(propertyFile)) {
                output.load(propertyReader);
            } catch (IOException e) {
                Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "<init>", e);
            }
        }
        return output;
    }
}
