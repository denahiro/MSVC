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
public class ServerStoppedControlPanel extends ControlPanel{

    private final JButton stateButton=new JButton();
    private final JButton updateButton=new JButton();
    
    private GroupLayout layout;
    
    public ServerStoppedControlPanel(MainGUI master) {
        super(master,ServerHandler.ServerState.STOPPED);
        this.initComponents();
    }
    
    private void initComponents()
    {
        this.stateButton.setText("launch server");
        this.updateButton.setText("update server");
        
        this.stateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerStoppedControlPanel.this.changeState(ServerHandler.ServerState.LAUNCHING);
            }
        });
        this.updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerStoppedControlPanel.this.changeState(ServerHandler.ServerState.UPDATING);
            }
        });
        
        this.layout=new GroupLayout(this);
        this.setLayout(this.layout);
        
        this.layout.setHorizontalGroup(this.layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.stateButton,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
                .addComponent(this.updateButton,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        
        this.layout.setVerticalGroup(this.layout.createSequentialGroup()
                .addComponent(this.stateButton)
                .addComponent(this.updateButton));
    }

    @Override
    protected void changeStateImpl(ServerState newState) {
        switch(newState)
        {
            case LAUNCHING:
                if(this.master.getServerHandler().isServerFilesReady()) {
                    this.master.setControlPanel(new ServerLaunchingControlPanel(master));
                } else {
                    this.master.setControlPanel(new ServerUpdatingControlPanel(master,ServerHandler.ServerState.LAUNCHING));
                }
                break;
            case UPDATING:
                this.master.setControlPanel(new ServerUpdatingControlPanel(master,ServerHandler.ServerState.STOPPED));
                break;
        }
    }

    @Override
    protected void remove() {
    }

    @Override
    public void run() {
    }
}
