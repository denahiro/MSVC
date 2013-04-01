/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server;

/**
 *
 * @author Denahiro
 */
public interface ServerStateListener {
    public void stateChangeEvent(ServerHandler.ServerState newState);
}
