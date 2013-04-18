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
public class ServerLaunchingControlPanel extends ControlPanel{
    
    private final JLabel launchingLabel=new JLabel("launching server...");
    
    public ServerLaunchingControlPanel(MainGUI master) {
        super(master,ServerHandler.ServerState.LAUNCHING);
                
        initComponents();
    }
    
    private void initComponents()
    {
        final GroupLayout myLayout= new GroupLayout(this);
        this.setLayout(myLayout);
        
        myLayout.setHorizontalGroup(myLayout.createParallelGroup()
                .addComponent(this.launchingLabel,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        
        myLayout.setVerticalGroup(myLayout.createSequentialGroup()
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
    public void run() {
        this.master.getServerHandler().launchServer();
    }
}
