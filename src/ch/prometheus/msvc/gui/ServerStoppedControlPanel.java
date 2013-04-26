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
import javax.swing.JDialog;

/**
 *
 * @author Denahiro
 */
public class ServerStoppedControlPanel extends ControlPanel{

    private final JButton launchButton=new JButton("launch server");
    private final JButton updateButton=new JButton("update server");
    private final JButton selectButton=new JButton("select server");
    private final JButton settingsButton=new JButton("server settings");
    
    public ServerStoppedControlPanel(MainGUI master) {
        super(master,ServerHandler.ServerState.STOPPED);
        this.initComponents();
    }
    
    private void initComponents()
    {        
        initButtons();
        
        final GroupLayout myLayout=new GroupLayout(this);
        this.setLayout(myLayout);
        
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.launchButton,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
                .addComponent(this.updateButton,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
                .addComponent(this.selectButton,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
                .addComponent(this.settingsButton,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
        
        myLayout.setVerticalGroup(myLayout.createSequentialGroup()
                .addComponent(this.launchButton)
                .addComponent(this.updateButton)
                .addComponent(this.selectButton)
                .addComponent(this.settingsButton));
    }

    @Override
    protected void changeStateImpl(ServerState newState) {
    }

    @Override
    public void run() {
    }

    private void initButtons() {
        this.launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ServerStoppedControlPanel.this.master.getServerHandler().isServerFilesReady()) {
                    ServerStoppedControlPanel.this.master.setControlPanel(
                        new ServerLaunchingControlPanel(ServerStoppedControlPanel.this.master));
                } else {
                    ServerStoppedControlPanel.this.master.setControlPanel(
                        new ServerUpdatingControlPanel(ServerStoppedControlPanel.this.master
                        ,ServerHandler.ServerState.LAUNCHING));
                }
            }
        });
        this.updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerStoppedControlPanel.this.master.setControlPanel(
                        new ServerUpdatingControlPanel(ServerStoppedControlPanel.this.master,ServerHandler.ServerState.STOPPED));
            }
        });
        this.selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog serverSelector=new ServerSelectionDialog(ServerStoppedControlPanel.this.master);
                serverSelector.setVisible(true);
            }
        });
        this.settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog serverSettings=new ServerSettingsDialog(ServerStoppedControlPanel.this.master);
                serverSettings.setVisible(true);
            }
        });
    }
}
