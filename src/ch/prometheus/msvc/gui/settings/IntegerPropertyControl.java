/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui.settings;

import ch.prometheus.msvc.gui.ServerSettingsDialog;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Denahiro
 */
public class IntegerPropertyControl extends PropertyControl{

    protected final JSpinner control=new JSpinner();
    protected final SpinnerNumberModel controlModel;

    public IntegerPropertyControl(PropertyHandler owner, String propertyName, int minValue, int maxValue) {
        super(owner, propertyName);

        this.controlModel=new SpinnerNumberModel(Integer.parseInt(this.owner.getProperty(this.propertyName)),
                minValue, maxValue, 1);

        this.control.setModel(this.controlModel);
        this.control.addChangeListener(new ControlChangeCallback());

        ((DefaultEditor) this.control.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEADING);
    }

    @Override
    protected String getControlContent() {
        return Integer.toString(this.controlModel.getNumber().intValue());
    }

    @Override
    public JComponent getControl() {
        return this.control;
    }

    protected class ControlChangeCallback extends PropertyControl.ControlCallback implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent ce) {
            this.actionPerformed(null);
        }
    }
}
