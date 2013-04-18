/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.server.ServerHandler.ServerState;
import java.util.Observable;
import java.util.Observer;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author Denahiro
 */
public class ServerUpdatingControlPanel extends ControlPanel {

    private final ServerHandler.ServerState nextState;
    private final JLabel updatingLabel=new JLabel("updating server ...");
    private final JProgressBar updatingProgress=new JProgressBar();
    
    private Observer progressBarObserver=new Observer() {
        @Override
        public void update(Observable o, Object o1) {
            ServerHandler.ProgressInfo progress=(ServerHandler.ProgressInfo) o1;
            if(ServerUpdatingControlPanel.this.updatingProgress.getMaximum()!=progress.total) {
                ServerUpdatingControlPanel.this.updatingProgress.setMaximum((int) progress.total);
            }
            ServerUpdatingControlPanel.this.updatingProgress.setValue((int)progress.current);
        }
    };
    
    public ServerUpdatingControlPanel(MainGUI master, ServerHandler.ServerState nextState) {
        super(master, ServerHandler.ServerState.UPDATING);
        this.nextState=nextState;
        this.initComponents();
        this.master.getServerHandler().updateStateObservable.addObserver(this.progressBarObserver);
    }

    private void initComponents()
    {
        final GroupLayout myLayout= new GroupLayout(this);
        this.setLayout(myLayout);
        
        myLayout.setHorizontalGroup(myLayout.createParallelGroup()
                .addComponent(this.updatingLabel,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
                .addComponent(this.updatingProgress,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
                
        myLayout.setVerticalGroup(myLayout.createSequentialGroup()
                .addComponent(this.updatingLabel)
                .addComponent(this.updatingProgress));
    }
    
    @Override
    protected void changeStateImpl(ServerState newState) {
    }

    @Override
    protected void removeListeners() {
        super.removeListeners();
        this.master.getServerHandler().updateStateObservable.deleteObserver(this.progressBarObserver);
    }

    @Override
    public void run() {
        this.master.getServerHandler().updateServer();
        switch(this.nextState)
        {
            case LAUNCHING:
                this.master.setControlPanel(new ServerLaunchingControlPanel(this.master));
                break;
            case STOPPED:
                this.master.setControlPanel(new ServerStoppedControlPanel(this.master));
                break;
        }
    }
    
}
