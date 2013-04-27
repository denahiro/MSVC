/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui.settings;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

/**
 *
 * @author Denahiro
 */
public class BooleanPropertyControl extends PropertyControl{

    protected final JCheckBox control=new JCheckBox();
    
    public BooleanPropertyControl(ServerSettingsDialog owner, String propertyName) {
        super(owner, propertyName);
        
        this.control.setSelected(Boolean.parseBoolean(this.owner.getProperty(this.propertyName)));
        this.control.addActionListener(new PropertyControl.ControlCallback());
    }

    @Override
    protected String getControlContent() {
        return Boolean.toString(this.control.isSelected());
    }

    @Override
    public JComponent getControl() {
        return this.control;
    }
}
