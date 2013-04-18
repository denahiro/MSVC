/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import javax.swing.GroupLayout;
import javax.swing.JLabel;

/**
 *
 * @author Denahiro
 */
public class ServerStoppingControlPanel extends ControlPanel{
    
    private final JLabel stoppingLabel=new JLabel("stopping server...");
    
    public ServerStoppingControlPanel(MainGUI master) {
        super(master,ServerHandler.ServerState.STOPPING);
                
        initComponents();
    }
    
    private void initComponents()
    {
        final GroupLayout myLayout= new GroupLayout(this);
        this.setLayout(myLayout);
        
        myLayout.setHorizontalGroup(myLayout.createParallelGroup()
                .addComponent(this.stoppingLabel,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        
        myLayout.setVerticalGroup(myLayout.createSequentialGroup()
                .addComponent(this.stoppingLabel));
    }
    
    @Override
    protected void changeStateImpl(ServerHandler.ServerState newState)
    {
        if(newState==ServerHandler.ServerState.STOPPED) {
            this.master.setControlPanel(new ServerStoppedControlPanel(master));
        }
    }

    @Override
    public void run() {
        this.master.getServerHandler().shutdownServer();
    }
}
