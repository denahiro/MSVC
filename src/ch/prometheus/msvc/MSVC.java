/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc;

import ch.prometheus.msvc.gui.MainGUI;
import java.io.IOException;

/**
 *
 * @author Denahiro
 */
public class MSVC {
    public static void main(String[] args) throws IOException, InterruptedException{
        MainGUI gui=new MainGUI();
        gui.setVisible(true);
    }
}
