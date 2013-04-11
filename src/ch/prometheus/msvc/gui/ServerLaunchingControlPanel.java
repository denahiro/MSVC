/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.server.ServerHandler.ServerState;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JLabel;

/**
 *
 * @author Denahiro
 */
public class ServerLaunchingControlPanel extends ControlPanel{
    
    private final JLabel launchingLabel=new JLabel("launching server...");
    
    private Observer serverStateObserver;
    
    public ServerLaunchingControlPanel(MainGUI master) {
        super(master,ServerHandler.ServerState.LAUNCHING);
        
        this.serverStateObserver=new Observer() {
            @Override
            public void update(Observable o, Object newState) {
                ServerLaunchingControlPanel.this.changeState((ServerState) newState);
            }
        };
        this.master.getServerHandler().serverStateObservable.addObserver(this.serverStateObserver);
                
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
            default:
                throw new IllegalArgumentException("newState was "+newState+" instead of "
                        +ServerHandler.ServerState.RUNNING+" or "+ServerHandler.ServerState.STOPPED+".");
        }
    }
    
    @Override
    protected void remove()
    {
        this.master.getServerHandler().serverStateObservable.deleteObserver(this.serverStateObserver);
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
