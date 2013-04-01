/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Denahiro
 */
public class ServerHandler {
    
    public enum ServerState {
        RUNNING,STOPPED,LAUNCHING,STOPPING
    }
    
    private ServerState currentState=ServerState.STOPPED;
    private final Object currentStateMutex=new Object();
    
    private final Collection<ServerStateListener> serverStateListeners;
    
    private Process serverInstance;
    private Communicator serverCom;
    private Thread comThread;
    private final PrintListener output;    
    
    public ServerHandler(PrintListener output){
        this.serverStateListeners = new ArrayList<>();
        this.output=output;
    }
    
    public void launchServer() throws IOException{
        synchronized(this) {
            if(this.getServerState()==ServerState.STOPPED) {
                this.updateServerState(ServerState.LAUNCHING);
                ProcessBuilder serverBuilder=new ProcessBuilder("java","-Xmx1024M","-Xms1024M","-jar","minecraft_server.jar","nogui");
                serverBuilder.directory(new File("server"));
                this.serverInstance=serverBuilder.start();
                this.serverCom=new Communicator(this.output, this.serverInstance);
                this.comThread=new Thread(this.serverCom);
                this.comThread.start();
                this.updateServerState(ServerState.RUNNING);
            } else {
                throw new IllegalStateException("Server already running.");
            }
        }
    }
    
    public void shutdownServer() {
        synchronized(this) {
            if(this.getServerState()==ServerState.RUNNING) {
                this.updateServerState(ServerState.STOPPING);
                this.serverCom.println("stop");
                try {
                    this.comThread.interrupt();
                    this.serverInstance.waitFor();
                    System.out.println(this.serverInstance.exitValue());
                    this.comThread.join();
                } catch(InterruptedException e) {

                } finally {
                    this.serverInstance=null;
                    this.comThread=null;
                    this.serverCom=null;
                    this.updateServerState(ServerState.STOPPED);
                }
            } else {
                throw new IllegalStateException("No server running.");
            }
        }
    }
    
    public void println(String line) {
        synchronized(this) {
            this.serverCom.println(line);
        }
    }
    
    private void updateServerState(ServerState newState) {
        synchronized(this.currentStateMutex) {
            this.currentState=newState;
            synchronized(this.serverStateListeners) {
                for(ServerStateListener ssl:this.serverStateListeners) {
                    ssl.stateChangeEvent(this.currentState);
                }
            }
        }
    }
    
    public void addServerStateListener(ServerStateListener toAdd) {
        synchronized(this.serverStateListeners) {
            this.serverStateListeners.add(toAdd);
        }
    }
    
    public ServerState getServerState() {
        synchronized(this.currentStateMutex) {
            return this.currentState;
        }
    }
}
