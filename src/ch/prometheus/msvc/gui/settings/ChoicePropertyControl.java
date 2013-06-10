/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui.settings;

import ch.prometheus.msvc.server.files.PropertyHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 *
 * @author Denahiro
 */
public class ChoicePropertyControl extends PropertyControl {

    protected final JComboBox<String> control = new JComboBox<>();
    protected final DefaultComboBoxModel<String> controlModel = new DefaultComboBoxModel<>();
    protected final Map<String, PropertyChoice> choicesMap = new HashMap<>();
    protected final Map<String, PropertyChoice> outputMap = new HashMap<>();

    public ChoicePropertyControl(PropertyHandler owner, String propertyName, List<PropertyChoice> choices) {
        super(owner, propertyName);

        for (PropertyChoice choice : choices) {
            this.choicesMap.put(choice.getTitle(), choice);
            this.outputMap.put(choice.getOutput(), choice);
            this.controlModel.addElement(choice.getTitle());
        }

        this.control.setModel(this.controlModel);
        this.control.setSelectedItem(this.outputMap.get(this.owner.getProperty(this.propertyName)).getTitle());
        this.control.addActionListener(new PropertyControl.ControlCallback());
    }

    @Override
    protected String getControlContent() {
        return this.choicesMap.get((String) this.controlModel.getSelectedItem()).getOutput();
    }

    @Override
    public JComponent getControl() {
        return this.control;
    }
}
