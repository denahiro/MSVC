/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import java.util.Observable;
import java.util.Observer;
import javax.swing.GroupLayout;
import javax.swing.JLabel;

/**
 *
 * @author Denahiro
 */
public class ServerStoppingControlPanel extends ControlPanel{
    
    private final JLabel stoppingLabel=new JLabel("stopping server...");
    
    private Observer serverStateObserver;
    
    public ServerStoppingControlPanel(MainGUI master) {
        super(master,ServerHandler.ServerState.STOPPING);
        this.serverStateObserver=new Observer() {
            @Override
            public void update(Observable o, Object newState) {
                ServerStoppingControlPanel.this.changeState((ServerHandler.ServerState) newState);
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
                .addComponent(this.stoppingLabel,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(this.stoppingLabel));
    }
    
    @Override
    protected void changeStateImpl(ServerHandler.ServerState newState)
    {
        if(newState==ServerHandler.ServerState.STOPPED) {
            this.master.setControlPanel(new ServerStoppedControlPanel(master));
        } else {
            throw new IllegalArgumentException("newState was "+newState+" instead of "+ServerHandler.ServerState.STOPPED+".");
        }
    }
    
    @Override
    protected void remove()
    {
        this.master.getServerHandler().serverStateObservable.deleteObserver(this.serverStateObserver);
    }

    @Override
    public void run() {
        this.master.getServerHandler().shutdownServer();
    }
}
