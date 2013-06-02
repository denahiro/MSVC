/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ExecutorHolder;
import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.util.event.EventListener;
import javax.swing.JPanel;

/**
 *
 * @author Denahiro
 */
public abstract class ControlPanel extends JPanel implements Runnable {

    protected final MainGUI master;
    protected final ServerHandler.ServerState currentState;
    private EventListener<ServerHandler.ServerStateChangeEvent> stateListener = new ChangeStateListener();

    public ControlPanel(MainGUI master, ServerHandler.ServerState currentState) {
        this.master = master;
        this.currentState = currentState;
        this.master.getServerHandler().serverStateObservable.addListener(this.stateListener);
    }

    public final void start() {
        if (!ExecutorHolder.EXECUTOR.isShutdown()) {
            ExecutorHolder.EXECUTOR.execute(this);
        }
    }

    private void changeState(ServerHandler.ServerState newState) {
        if (newState != this.currentState) {
            this.removeListeners();
            this.changeStateImpl(newState);
        }
    }

    protected abstract void changeStateImpl(ServerHandler.ServerState newState);

    protected void removeListeners() {
        this.master.getServerHandler().serverStateObservable.removeListener(this.stateListener);
    }

    private class ChangeStateListener implements EventListener<ServerHandler.ServerStateChangeEvent> {

        @Override
        public void onEvent(ServerHandler.ServerStateChangeEvent event) {
            ControlPanel.this.changeState(event.getNewState());
        }
    }
}
