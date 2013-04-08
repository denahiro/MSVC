/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Denahiro
 */
public class ServerHandler {
    
    public final static File SERVER_DIRECTORY=new File("server");
    public final static File SERVER_JAR=new File(SERVER_DIRECTORY,"minecraft_server.jar");
    public final URI SERVER_SOURCE= URI.create("https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar");
    
    public enum ServerState {
        RUNNING,STOPPED,LAUNCHING,STOPPING,UPDATING
    }
    
    private ServerState currentState=ServerState.STOPPED;
    private final Object currentStateMutex=new Object();
    
    private final Collection<ServerStateListener> serverStateListeners = new ArrayList<>();
    private final Collection<ServerStateListener> serverStateListenersToAdd = new ArrayList<>();
    private final Collection<ServerStateListener> serverStateListenersToRemove = new ArrayList<>();
    
    private Process serverInstance;
    private Communicator serverCom;
    private Thread comThread;
    private final PrintListener output;    
    
    public ServerHandler(PrintListener output){
        this.output=output;
    }
    
    public void launchServer() throws IOException{
        synchronized(this) {
            if(this.getServerState()==ServerState.STOPPED) {
                assert this.isServerFilesReady();
                this.updateServerState(ServerState.LAUNCHING);
                ProcessBuilder serverBuilder=new ProcessBuilder("java","-Xmx1024M","-Xms1024M","-jar",SERVER_JAR.getName(),"nogui");
                serverBuilder.directory(SERVER_DIRECTORY);
                this.serverInstance=serverBuilder.start();
                this.serverCom=new Communicator(this.output, this.serverInstance);
                this.comThread=new Thread(this.serverCom);
                this.comThread.start();
                this.updateServerState(ServerState.RUNNING);
            } else {
                throw new IllegalStateException("Can't launch server.");
            }
        }
    }
    
    public void shutdownServer() {
        synchronized(this) {
            if(this.getServerState()==ServerState.RUNNING) {
                this.updateServerState(ServerState.STOPPING);
                this.serverCom.println("stop");
                try {
                    this.serverInstance.waitFor();
                    this.comThread.join();
                } catch(InterruptedException e) {

                } finally {
                    this.serverInstance=null;
                    this.comThread=null;
                    this.serverCom=null;
                    this.updateServerState(ServerState.STOPPED);
                }
            } else {
                throw new IllegalStateException("Can't stop server.");
            }
        }
    }
    
    public boolean isServerFilesReady() {
        return SERVER_DIRECTORY.exists() && SERVER_JAR.exists();
    }
    
    public void readyServerFiles() throws IOException{
        if(this.isServerFilesReady()) {
            SERVER_DIRECTORY.mkdir();
            updateServer();
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
                for(ServerStateListener ssl:this.serverStateListenersToAdd) {
                    this.serverStateListeners.add(ssl);
                }
                this.serverStateListenersToAdd.clear();
                for(ServerStateListener ssl:this.serverStateListenersToRemove) {
                    this.serverStateListeners.remove(ssl);
                }
                this.serverStateListenersToRemove.clear();
            }
        }
    }
    
    public void addServerStateListener(ServerStateListener toAdd) {
        synchronized(this.serverStateListeners) {
            this.serverStateListenersToAdd.add(toAdd);
        }
    }
    
    public void removeServerStateListener(ServerStateListener toRemove) {
        synchronized(this.serverStateListeners) {
            this.serverStateListenersToRemove.add(toRemove);
        }
    }
    
    public ServerState getServerState() {
        synchronized(this.currentStateMutex) {
            return this.currentState;
        }
    }
    
    public void updateServer() throws IOException{
        synchronized(this) {
            if(this.getServerState()==ServerState.STOPPED) {
                SERVER_DIRECTORY.mkdir();
                try (InputStream netIn = SERVER_SOURCE.toURL().openStream();
                    OutputStream fileOut=new FileOutputStream(ServerHandler.SERVER_JAR)) {
                    updateServerState(ServerState.UPDATING);
                    int data;
                    while((data=netIn.read())>=0) {
                        fileOut.write(data);
                    }
                } finally {
                    updateServerState(ServerState.STOPPED);
                }
            }  else {
                throw new IllegalStateException("Can't update server.");
            }
        }
    }
}
