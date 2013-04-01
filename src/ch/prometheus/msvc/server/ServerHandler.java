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
import java.util.logging.Level;
import java.util.logging.Logger;

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
                this.readyServerFiles();
                this.updateServerState(ServerState.LAUNCHING);
                ProcessBuilder serverBuilder=new ProcessBuilder("java","-Xmx1024M","-Xms1024M","-jar",SERVER_JAR.getName(),"nogui");
                serverBuilder.directory(SERVER_DIRECTORY);
                this.serverInstance=serverBuilder.start();
                this.serverCom=new Communicator(this.output, this.serverInstance);
                this.comThread=new Thread(this.serverCom);
                this.comThread.start();
                this.updateServerState(ServerState.RUNNING);
            }
        }
    }
    
    public void shutdownServer() {
        synchronized(this) {
            if(this.getServerState()==ServerState.RUNNING) {
                this.updateServerState(ServerState.STOPPING);
                this.serverCom.println("stop");
                try {
//                    this.comThread.interrupt();
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
            }
        }
    }
    
    public void readyServerFiles() throws IOException{
        if(!SERVER_DIRECTORY.exists()) {
            SERVER_DIRECTORY.mkdir();
        }
        if(!SERVER_JAR.exists()) {
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
    
    public void updateServer() throws IOException{
        synchronized(this) {
            if(this.getServerState()==ServerState.STOPPED) {
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
            }
        }
    }
}
