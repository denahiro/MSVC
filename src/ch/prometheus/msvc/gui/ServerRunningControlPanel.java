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

    private final JButton stopButton=new JButton("stop server");
    
    public ServerRunningControlPanel(MainGUI master) {
        super(master, ServerHandler.ServerState.RUNNING);
        this.initComponents();
    }
    
    private void initComponents() {        
        initStopButton();
        
        final GroupLayout myLayout = new GroupLayout(this);
        
        this.setLayout(myLayout);
        
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.stopButton,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        
        myLayout.setVerticalGroup(myLayout.createSequentialGroup()
                .addComponent(this.stopButton));
    }

    
    @Override
    protected void changeStateImpl(ServerState newState) {
        if(newState==ServerState.STOPPED)
        {
            this.master.setControlPanel(new ServerStoppedControlPanel(master));
        }
    }

    @Override
    public void run() {
        
    }

    private void initStopButton() {
        this.stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerRunningControlPanel.this.master.setControlPanel(
                        new ServerStoppingControlPanel(ServerRunningControlPanel.this.master));
            }
        });
    }
    
}
