/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui.settings;

import ch.prometheus.msvc.gui.ServerSettingsDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author Denahiro
 */
public abstract class PropertyControl {
    private final JLabel myLabel;
    protected final PropertyHandler owner;
    protected final String propertyName;

    public PropertyControl(PropertyHandler owner, String propertyName) {
        this.propertyName = propertyName;
        this.owner = owner;
        this.myLabel = new JLabel(propertyName, SwingConstants.TRAILING);
    }

    protected abstract String getControlContent();

    public JLabel getLabel() {
        return this.myLabel;
    }

    public abstract JComponent getControl();

    protected class ControlCallback implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            PropertyControl.this.owner.setProperty(PropertyControl.this.propertyName,
                    PropertyControl.this.getControlContent());
        }
    }
}
