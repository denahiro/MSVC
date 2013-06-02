/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.gui;

import ch.prometheus.msvc.server.ServerHandler;
import ch.prometheus.msvc.server.ServerHandler.ServerState;
import ch.prometheus.msvc.util.ProgressEvent;
import ch.prometheus.msvc.util.event.EventListener;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author Denahiro
 */
public class ServerUpdatingControlPanel extends ControlPanel {

    private final ServerHandler.ServerState nextState;
    private final JLabel updatingLabel = new JLabel("updating server ...");
    private final JProgressBar updatingProgress = new JProgressBar();
    private final EventListener<ProgressEvent> progressBarListener = new UpdateProgressListener();

    public ServerUpdatingControlPanel(MainGUI master, ServerHandler.ServerState nextState) {
        super(master, ServerHandler.ServerState.UPDATING);
        this.nextState = nextState;
        this.initComponents();
        this.master.getServerHandler().updateStateObservable.addListener(this.progressBarListener);
    }

    private void initComponents() {
        final GroupLayout myLayout = new GroupLayout(this);
        this.setLayout(myLayout);

        myLayout.setHorizontalGroup(myLayout.createParallelGroup()
                .addComponent(this.updatingLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(this.updatingProgress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

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
        this.master.getServerHandler().updateStateObservable.removeListener(this.progressBarListener);
    }

    @Override
    public void run() {
        this.master.getServerHandler().updateServer();
        switch (this.nextState) {
            case LAUNCHING:
                this.master.setControlPanel(new ServerLaunchingControlPanel(this.master));
                break;
            case STOPPED:
                this.master.setControlPanel(new ServerStoppedControlPanel(this.master));
                break;
        }
    }

    private class UpdateProgressListener implements EventListener<ProgressEvent> {

        @Override
        public void onEvent(ProgressEvent event) {
            if (ServerUpdatingControlPanel.this.updatingProgress.getMaximum() != event.getTotal()) {
                ServerUpdatingControlPanel.this.updatingProgress.setMaximum((int) event.getTotal());
            }
            ServerUpdatingControlPanel.this.updatingProgress.setValue((int) event.getCurrent());
        }
    }
}
