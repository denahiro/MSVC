/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import javax.swing.JPanel;

/**
 *
 * @author Denahiro
 */
public abstract class ControlPanel extends JPanel implements Runnable {
    
    protected final MainGUI master;
    
    protected final ServerHandler.ServerState currentState;
    
    protected final Thread myThread=new Thread(this);
    
    public ControlPanel(MainGUI master,ServerHandler.ServerState currentState) {
        this.master=master;
        this.currentState=currentState;
    }
    
    public final void start()
    {
        this.myThread.start();
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
