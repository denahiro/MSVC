/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui.settings;

import ch.prometheus.msvc.gui.ServerSettingsDialog;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author Denahiro
 */
public class TextPropertyControl extends PropertyControl{

    protected final JTextField control=new JTextField();

    public TextPropertyControl(PropertyHandler owner, String propertyName) {
        super(owner, propertyName);

        this.control.setText(this.owner.getProperty(this.propertyName));
        this.control.addActionListener(new PropertyControl.ControlCallback());
    }

    @Override
    protected String getControlContent() {
        return this.control.getText();
    }

    @Override
    public JComponent getControl() {
        return this.control;
    }
}
