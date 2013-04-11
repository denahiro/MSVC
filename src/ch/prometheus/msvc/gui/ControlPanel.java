/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ExecutorHolder;
import ch.prometheus.msvc.server.ServerHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JPanel;

/**
 *
 * @author Denahiro
 */
public abstract class ControlPanel extends JPanel implements Runnable {
    
    protected final MainGUI master;
    
    protected final ServerHandler.ServerState currentState;
    
    public ControlPanel(MainGUI master,ServerHandler.ServerState currentState) {
        this.master=master;
        this.currentState=currentState;
    }
    
    public final void start()
    {
        ExecutorHolder.EXECUTOR.execute(this);
    }
    
    protected final void changeState(ServerHandler.ServerState newState)
    {
        if(newState!=this.currentState) {
            this.remove();
            this.changeStateImpl(newState);
        }
    }
    
    protected abstract void changeStateImpl(ServerHandler.ServerState newState);
    protected abstract void remove();
}
