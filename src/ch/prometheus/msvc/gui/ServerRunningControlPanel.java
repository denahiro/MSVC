/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.server.ServerHandler.ServerState;

/**
 *
 * @author Denahiro
 */
public class ServerRunningControlPanel extends ControlPanel{

    public ServerRunningControlPanel(MainGUI master) {
        super(master, ServerHandler.ServerState.RUNNING);
    }

    
    @Override
    protected void changeStateImpl(ServerState newState) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
