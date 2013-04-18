/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Denahiro
 */
public class ServerHandler {
    
    public final static File SERVER_DIRECTORY=new File("server");
    public final static File SERVER_JAR=new File(SERVER_DIRECTORY,"minecraft_server.jar");
    public final static URI SERVER_SOURCE= URI.create("https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar");
    public final static long DOWNLOAD_RESOLUTION=100;

    
    public enum ServerState {
        RUNNING,STOPPED,LAUNCHING,STOPPING,UPDATING
    }
    
    private ServerState currentState=ServerState.STOPPED;
    private final Object currentStateMutex=new Object();
    
    public final Observable serverStateObservable=new AlwaysNotifyObservable();
    public final Observable updateStateObservable=new AlwaysNotifyObservable();
    
    private Process serverInstance;
    private Communicator serverCom;
    private final PrintListener output;    
    
    public ServerHandler(PrintListener output){
        this.output=output;
    }
    
    public void launchServer(){
        synchronized(this) {
            if(this.getServerState()==ServerState.STOPPED) {
                assert this.isServerFilesReady();
                launchServerCore();
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
                this.serverCom=null;
            } else {
                throw new IllegalStateException("Can't stop server.");
            }
        }
    }
    
    public boolean isServerFilesReady() {
        synchronized(this) {
            return SERVER_DIRECTORY.exists() && SERVER_JAR.exists();
        }
    }
    
    public void println(String line) {
        synchronized(this) {
            if(this.getServerState()==ServerState.RUNNING)
            {
                this.serverCom.println(line);
            } else {
                output.println("The server needs to be running to be able to send commands.");
            }
        }
    }

    private void launchServerCore() {
        this.updateServerState(ServerState.LAUNCHING);
        try{
            startServerProcess();
            ExecutorHolder.EXECUTOR.execute(new ServerShutdownWaiter());
            startServerCommunicator();
            this.updateServerState(ServerState.RUNNING);
        } catch (IOException e) {
            Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "launchServerCore()", e);
            this.output.println(e.toString());
            this.updateServerState(ServerState.STOPPED);
        }
    }
    
    private void updateServerState(ServerState newState) {
        synchronized(this.currentStateMutex) {
            this.currentState=newState;
            this.serverStateObservable.notifyObservers(this.currentState);
        }
    }
    
    public ServerState getServerState() {
        synchronized(this.currentStateMutex) {
            return this.currentState;
        }
    }
    
    public void updateServer(){
        synchronized(this) {
            if(this.getServerState()==ServerState.STOPPED) {
                updateServerState(ServerState.UPDATING);
                updateServerCore();
            }  else {
                throw new IllegalStateException("Can't update server.");
            }
        }
    }
    private void startServerProcess() throws IOException {
        ProcessBuilder serverBuilder=new ProcessBuilder("java","-Xmx1024M","-Xms1024M","-jar",SERVER_JAR.getName(),"nogui");
        serverBuilder.directory(SERVER_DIRECTORY);
        this.serverInstance=serverBuilder.start();
    }

    private void startServerCommunicator() {
        this.serverCom=new Communicator(this.output, this.serverInstance);
        ExecutorHolder.EXECUTOR.execute(this.serverCom);
    }

    private void updateServerCore() {
        SERVER_DIRECTORY.mkdir();
        BufferedInputStream netIn=null;
        OutputStream fileOut=null;
        try{
            URLConnection connection=SERVER_SOURCE.toURL().openConnection();
            long dataTotal=connection.getContentLengthLong();
            netIn = new BufferedInputStream(connection.getInputStream());
            fileOut=new FileOutputStream(ServerHandler.SERVER_JAR);
            transferUpdateData(dataTotal, netIn, fileOut);
        } catch(IOException e) {
            Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "updateServerCore()", e);
            this.output.println(e.toString());
        } finally {
            if(netIn!=null) {
                try{
                    netIn.close();
                } catch(IOException e) {}
            }
            if(fileOut!=null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, e);
                    this.output.println(e.toString());
                }
            }
            this.updateServerState(ServerState.STOPPED);
        }
    }

    private void transferUpdateData(long dataTotal, BufferedInputStream netIn, OutputStream fileOut) throws IOException {
        long dataAmount=0;
        int divisor=(int) (dataTotal/DOWNLOAD_RESOLUTION);
        byte[] data=new byte[divisor];
        int bytesRead;
        while((bytesRead=netIn.read(data, 0, divisor))>=0) {
            fileOut.write(data,0,bytesRead);
            dataAmount+=bytesRead;
            this.updateStateObservable.notifyObservers(new ProgressInfo(dataAmount, dataTotal));
        }
    }
    
    private class ServerShutdownWaiter implements Runnable {
        @Override
        public void run() {
            try {
                ServerHandler.this.serverInstance.waitFor();
            } catch(InterruptedException ie) {
                
            } finally {
                ServerHandler.this.updateServerState(ServerState.STOPPED);
            }
        }
    }
    
    public static class AlwaysNotifyObservable extends Observable {
        @Override
        public void notifyObservers(Object o) {
            super.setChanged();
            super.notifyObservers(o);
        }
    }
    
    public static class ProgressInfo {
        public final long current;
        public final long total;

        public ProgressInfo(long current, long total) {
            this.current = current;
            this.total = total;
        }
    }
}
