/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ExecutorHolder;
import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.server.ServerHandler.ServerState;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

/**
 *
 * @author Denahiro
 */
public abstract class ControlPanel extends JPanel implements Runnable {
    
    protected final MainGUI master;
    
    protected final ServerHandler.ServerState currentState;
    private Observer stateListener;
    
    public ControlPanel(MainGUI master,ServerHandler.ServerState currentState) {
        this.master=master;
        this.currentState=currentState;
        
        this.stateListener=new Observer() {
            @Override
            public void update(Observable o, Object newState) {
                ControlPanel.this.changeState((ServerState)newState);
            }
        };
        this.master.getServerHandler().serverStateObservable.addObserver(this.stateListener);
    }
    
    public final void start()
    {
        if(!ExecutorHolder.EXECUTOR.isShutdown())
        {
            ExecutorHolder.EXECUTOR.execute(this);
        }
    }
    
    private void changeState(ServerHandler.ServerState newState)
    {
        if(newState!=this.currentState) {
            this.removeListeners();
            this.changeStateImpl(newState);
        }
    }
    
    protected abstract void changeStateImpl(ServerHandler.ServerState newState);
    
    protected void removeListeners(){
        this.master.getServerHandler().serverStateObservable.deleteObserver(this.stateListener);
    }
}
