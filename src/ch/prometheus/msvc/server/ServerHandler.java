/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server;

import ch.prometheus.msvc.util.FiniteStreamConnector;
import ch.prometheus.msvc.util.ProgressEvent;
import ch.prometheus.msvc.util.event.Event;
import ch.prometheus.msvc.util.event.EventSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.logging.Logger;

/**
 *
 * @author Denahiro
 */
public class ServerHandler {

    public final static File SERVER_DIRECTORY = new File("server");
    public final static File SERVER_JAR = new File(SERVER_DIRECTORY, "minecraft_server.jar");
    public final static URI SERVER_SOURCE = URI.create("https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar");
    public final static long DOWNLOAD_RESOLUTION = 100;
    private ServerState currentState = ServerState.STOPPED;
    private final Object currentStateMutex = new Object();
    public final EventSource<ServerStateChangeEvent> serverStateObservable = new EventSource<>();
    public final EventSource<ProgressEvent> updateStateObservable = new EventSource<>();
    private Process serverInstance;
    private Communicator serverCom;
    private final PrintListener output;

    public ServerHandler(PrintListener output) {
        this.output = output;
    }

    public void launchServer() {
        synchronized (this) {
            if (this.getServerState() == ServerState.STOPPED) {
                assert this.isServerFilesReady();
                launchServerCore();
            } else {
                throw new IllegalStateException("Can't launch server.");
            }
        }
    }

    public void shutdownServer() {
        synchronized (this) {
            if (this.getServerState() == ServerState.RUNNING) {
                this.updateServerState(ServerState.STOPPING);
                this.serverCom.println("stop");
                this.serverCom = null;
            } else {
                throw new IllegalStateException("Can't stop server.");
            }
        }
    }

    public boolean isServerFilesReady() {
        synchronized (this) {
            return SERVER_DIRECTORY.exists() && SERVER_JAR.exists();
        }
    }

    public void println(String line) {
        synchronized (this) {
            if (this.getServerState() == ServerState.RUNNING) {
                this.serverCom.println(line);
            } else {
                output.println("The server needs to be running to be able to send commands.");
            }
        }
    }

    private void launchServerCore() {
        this.updateServerState(ServerState.LAUNCHING);
        try {
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
        synchronized (this.currentStateMutex) {
            this.currentState = newState;
        }
        this.serverStateObservable.fireEvent(new ServerStateChangeEvent(this, newState));
    }

    public ServerState getServerState() {
        synchronized (this.currentStateMutex) {
            return this.currentState;
        }
    }

    public void updateServer() {
        synchronized (this) {
            if (this.getServerState() == ServerState.STOPPED) {
                updateServerState(ServerState.UPDATING);
                updateServerCore();
            } else {
                throw new IllegalStateException("Can't update server.");
            }
        }
    }

    private void startServerProcess() throws IOException {
        ProcessBuilder serverBuilder = new ProcessBuilder("java", "-Xmx1024M", "-Xms1024M", "-jar", SERVER_JAR.getName(), "nogui");
        serverBuilder.directory(SERVER_DIRECTORY);
        this.serverInstance = serverBuilder.start();
    }

    private void startServerCommunicator() {
        this.serverCom = new Communicator(this.output, this.serverInstance);
        ExecutorHolder.EXECUTOR.execute(this.serverCom);
    }

    private void updateServerCore() {
        SERVER_DIRECTORY.mkdir();
        try {
            URLConnection connection = SERVER_SOURCE.toURL().openConnection();
            int dataTotal = (int) connection.getContentLengthLong();
            InputStream netIn = connection.getInputStream();
            OutputStream fileOut = new FileOutputStream(ServerHandler.SERVER_JAR);
            FiniteStreamConnector connector = new FiniteStreamConnector(netIn, fileOut, dataTotal)
                    .setProgressEventSource(updateStateObservable);
            connector.call();
        } catch (IOException e) {
            Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "updateServerCore()", e);
            this.output.println(e.toString());
        } finally {
            this.updateServerState(ServerState.STOPPED);
        }
    }

    public enum ServerState {

        RUNNING, STOPPED, LAUNCHING, STOPPING, UPDATING
    }

    private class ServerShutdownWaiter implements Runnable {

        @Override
        public void run() {
            try {
                ServerHandler.this.serverInstance.waitFor();
            } catch (InterruptedException ie) {
            } finally {
                ServerHandler.this.updateServerState(ServerState.STOPPED);
            }
        }
    }

    public class ServerStateChangeEvent extends Event<ServerHandler> {

        private final ServerState newState;

        public ServerStateChangeEvent(ServerHandler sender, ServerState newState) {
            super(sender);
            this.newState = newState;
        }

        public ServerState getNewState() {
            return newState;
        }
    }
}
