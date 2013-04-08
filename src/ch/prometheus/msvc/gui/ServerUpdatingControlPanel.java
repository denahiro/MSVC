/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.server.ServerHandler.ServerState;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JLabel;

/**
 *
 * @author Denahiro
 */
public class ServerUpdatingControlPanel extends ControlPanel {

    private final ServerHandler.ServerState nextState;
    private final JLabel updatingLabel=new JLabel("updating server ...");
    
    public ServerUpdatingControlPanel(MainGUI master, ServerHandler.ServerState nextState) {
        super(master, ServerHandler.ServerState.UPDATING);
        this.nextState=nextState;
        this.initComponents();
    }

    private void initComponents()
    {
        GroupLayout layout= new GroupLayout(this);
        this.setLayout(layout);
        
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(this.updatingLabel,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(this.updatingLabel));
    }
    
    @Override
    protected void changeStateImpl(ServerState newState) {
        switch(newState)
        {
            case LAUNCHING:
                this.master.setControlPanel(new ServerLaunchingControlPanel(master));
                break;
            case STOPPED:
                this.master.setControlPanel(new ServerStoppedControlPanel(master));
                break;
        }
    }

    @Override
    protected void remove() {
    }

    @Override
    public void run() {
        try {
            this.master.getServerHandler().updateServer();
        } catch(IOException e) {
            Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "run", e);
            this.changeState(ServerHandler.ServerState.STOPPED);
        }
        this.changeState(this.nextState);
    }
    
}
