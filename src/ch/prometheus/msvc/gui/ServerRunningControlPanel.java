/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.server.ServerHandler.ServerState;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;

/**
 *
 * @author Denahiro
 */
public class ServerRunningControlPanel extends ControlPanel{

    private final JButton stateButton=new JButton("stop server");
    
    private GroupLayout layout=new GroupLayout(this);
    
    public ServerRunningControlPanel(MainGUI master) {
        super(master, ServerHandler.ServerState.RUNNING);
        this.initComponents();
    }
    
    private void initComponents() {        
        this.stateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerRunningControlPanel.this.changeState(ServerHandler.ServerState.STOPPING);
            }
        });
        
        this.setLayout(this.layout);
        
        this.layout.setHorizontalGroup(this.layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.stateButton,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        
        this.layout.setVerticalGroup(this.layout.createSequentialGroup()
                .addComponent(this.stateButton));
    }

    
    @Override
    protected void changeStateImpl(ServerState newState) {
        switch(newState)
        {
            case STOPPING:
                this.master.setControlPanel(new ServerStoppingControlPanel(master));
                break;
            case STOPPED:
                this.master.setControlPanel(new ServerStoppedControlPanel(master));
                break;
            default:
                throw new IllegalArgumentException("newState was "+newState+" instead of "
                        +ServerHandler.ServerState.STOPPING+" or "+ServerHandler.ServerState.STOPPED+".");
        }
    }

    @Override
    protected void remove() {
        
    }

    @Override
    public void run() {
        
    }
    
}
