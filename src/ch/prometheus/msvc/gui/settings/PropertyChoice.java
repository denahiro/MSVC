/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui.settings;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Denahiro
 */
public class PropertyChoice {
    public static final List<PropertyChoice> difficultyChoices=Arrays.asList(
        new PropertyChoice("0 - Peaceful", Integer.toString(0)),
        new PropertyChoice("1 - Easy", Integer.toString(1)),
        new PropertyChoice("2 - Normal", Integer.toString(2)),
        new PropertyChoice("3 - Hard", Integer.toString(3)));
        
    public static final List<PropertyChoice> gamemodeChoices=Arrays.asList(
        new PropertyChoice("0 - Survival Mode", Integer.toString(0)),
        new PropertyChoice("1 - Creative Mode", Integer.toString(1)),
        new PropertyChoice("2 - Adventure Mode", Integer.toString(2)));
    
    private final String title;
    private final String output;

    public PropertyChoice(String title, String output) {
        this.title = title;
        this.output = output;
    }

    public String getTitle() {
        return title;
    }

    public String getOutput() {
        return output;
    }
}