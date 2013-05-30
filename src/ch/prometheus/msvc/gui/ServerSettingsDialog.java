/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.gui.settings.BooleanPropertyControl;
import ch.prometheus.msvc.gui.settings.ChoicePropertyControl;
import ch.prometheus.msvc.gui.settings.IntegerPropertyControl;
import ch.prometheus.msvc.gui.settings.PropertyChoice;
import ch.prometheus.msvc.gui.settings.TextPropertyControl;
import java.awt.Window;

/**
 *
 * @author Denahiro
 */
public class ServerSettingsDialog extends SettingsDialog {

    public ServerSettingsDialog(Window owner) {
        super(owner, "Server Settings");

        initHeading();

        initPropertyList();

        arrange();
    }

    private void initHeading() {
        addHeading("Current World: "+properties.getProperty("level-name"));
    }

    private void initPropertyList() {
        addPropertyControl(new BooleanPropertyControl(properties, "allow-nether"));
        addPropertyControl(new BooleanPropertyControl(properties, "allow-flight"));
        addPropertyControl(new IntegerPropertyControl(properties, "server-port", 1024, 65535));
        addPropertyControl(new TextPropertyControl(properties, "server-ip"));
        addPropertyControl(new IntegerPropertyControl(properties, "max-build-height", 0, 256));
        addPropertyControl(new BooleanPropertyControl(properties, "spawn-npcs"));
        addPropertyControl(new BooleanPropertyControl(properties, "white-list"));
        addPropertyControl(new BooleanPropertyControl(properties, "spawn-animals"));
        addPropertyControl(new BooleanPropertyControl(properties, "hardcore"));
        addPropertyControl(new TextPropertyControl(properties, "texture-pack"));
        addPropertyControl(new BooleanPropertyControl(properties, "online-mode"));
        addPropertyControl(new BooleanPropertyControl(properties, "pvp"));
        addPropertyControl(new ChoicePropertyControl(properties, "difficulty", PropertyChoice.difficultyChoices));
        addPropertyControl(new ChoicePropertyControl(properties, "gamemode", PropertyChoice.gamemodeChoices));
        addPropertyControl(new IntegerPropertyControl(properties, "max-players", 1, Integer.MAX_VALUE));
        addPropertyControl(new BooleanPropertyControl(properties, "spawn-monsters"));
        addPropertyControl(new IntegerPropertyControl(properties, "view-distance", 1, Integer.MAX_VALUE));
        addPropertyControl(new BooleanPropertyControl(properties, "generate-structures"));
        addPropertyControl(new TextPropertyControl(properties, "motd"));
    }
}
