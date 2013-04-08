/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.server.ServerHandler.ServerState;
import ch.prometheus.msvc.server.ServerStateListener;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JLabel;

/**
 *
 * @author Denahiro
 */
public class ServerLaunchingControlPanel extends ControlPanel{
    
    private final JLabel launchingLabel=new JLabel("launching server...");
    
    private ServerStateListener serverListener;
    
    public ServerLaunchingControlPanel(MainGUI master) {
        super(master,ServerHandler.ServerState.LAUNCHING);
        
        this.serverListener=new ServerStateListener() {
            @Override
            public void stateChangeEvent(ServerState newState) {
                ServerLaunchingControlPanel.this.changeState(newState);
            }
        };
        this.master.getServerHandler().addServerStateListener(this.serverListener);
                
        initComponents();
    }
    
    private void initComponents()
    {
        
        GroupLayout layout= new GroupLayout(this);
        this.setLayout(layout);
        
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(this.launchingLabel,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(this.launchingLabel));
    }
    
    @Override
    protected void changeStateImpl(ServerHandler.ServerState newState)
    {
        switch(newState)
        {
            case RUNNING:
                this.master.setControlPanel(new ServerRunningControlPanel(master));
                break;
            case STOPPED:
                this.master.setControlPanel(new ServerStoppedControlPanel(master));
                break;
        }
    }
    
    @Override
    protected void remove()
    {
        this.master.getServerHandler().removeServerStateListener(this.serverListener);
    }

    @Override
    public void run() {
        try {
            this.master.getServerHandler().launchServer();
        } catch(IOException e) {
            Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "run", e);
            this.changeState(ServerHandler.ServerState.STOPPED);
        }
    }
}
