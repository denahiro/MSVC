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
        GroupLayout layout= new GroupLayout(this);
        this.setLayout(layout);
        
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(this.updatingLabel,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
                .addComponent(this.updatingProgress,GroupLayout.PREFERRED_SIZE,GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE));
                
        
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(this.updatingLabel)
                .addComponent(this.updatingProgress));
    }
    
    @Override
    protected void changeStateImpl(ServerState newState) {
        switch(newState)
        {
            case LAUNCHING:
                this.master.setControlPanel(new ServerLaunchingControlPanel(master));
                break;
            case STOPPED:
                this.master.setControlPanel(new ServerStoppedControlPanel(master));
                break;
            default:
                throw new IllegalArgumentException("newState was "+newState+" instead of "
                        +ServerHandler.ServerState.LAUNCHING+" or "+ServerHandler.ServerState.STOPPED+".");
        }
    }

    @Override
    protected void remove() {
        this.master.getServerHandler().updateStateObservable.deleteObserver(this.progressBarObserver);
    }

    @Override
    public void run() {
        try {
            this.master.getServerHandler().updateServer();
        } catch(IOException e) {
            Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "run", e);
            this.changeState(ServerHandler.ServerState.STOPPED);
        }
        this.changeState(this.nextState);
    }
    
}
