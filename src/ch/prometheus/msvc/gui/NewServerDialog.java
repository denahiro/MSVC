/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.gui.settings.ChoicePropertyControl;
import ch.prometheus.msvc.gui.settings.PropertyChoice;
import ch.prometheus.msvc.gui.settings.TextPropertyControl;
import java.awt.Window;

/**
 *
 * @author Denahiro
 */
public class NewServerDialog extends SettingsDialog {

    public NewServerDialog(Window owner) {
        super(owner, "New Server");

        addHeading("World Creation Settings");

        initPropertyList();

        arrange();
    }

    private void initPropertyList() {
        this.addPropertyControl(new TextPropertyControl(properties, "level-name"));
        this.addPropertyControl(new ChoicePropertyControl(properties, "level-type",PropertyChoice.levelTypeChoices));
        this.addPropertyControl(new TextPropertyControl(properties, "level-seed"));
    }
}
