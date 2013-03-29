/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 * @author Denahiro
 */
public class ServerHandler {
    
    private Process serverInstance=null;
    private PrintWriter serverInput=null;
    private java.io.BufferedReader serverOutput=null;
    
    private ServerHandler(){
        
    }
    
    private static class Holder {
        public final static ServerHandler INSTANCE=new ServerHandler();
    }
    
    public static void launchServer() throws IOException{
        if(Holder.INSTANCE.serverInstance==null) {
            ProcessBuilder serverBuilder=new ProcessBuilder("java","-Xmx1024M","-Xms1024M","-jar","minecraft_server.jar","nogui");
            serverBuilder.directory(new File("server"));
            Holder.INSTANCE.serverInstance=serverBuilder.start();
            Holder.INSTANCE.serverInput=new PrintWriter(Holder.INSTANCE.serverInstance.getOutputStream());
            Holder.INSTANCE.serverOutput=new BufferedReader(new InputStreamReader(Holder.INSTANCE.serverInstance.getInputStream()));
        } else {
            throw new IllegalStateException("Server already running.");
        }
    }
    
    public static void shutdownServer() {
        if(Holder.INSTANCE.serverInstance!=null) {
            writeLine("stop");
            try {
                Holder.INSTANCE.serverInstance.waitFor();
                System.out.println(Holder.INSTANCE.serverInstance.exitValue());
            } catch(InterruptedException e) {
                
            } finally {
                Holder.INSTANCE.serverInstance=null;
                Holder.INSTANCE.serverInput.close();
                Holder.INSTANCE.serverInput=null;
                try {
                    Holder.INSTANCE.serverOutput.close();
                } catch (IOException e) {
                }
                Holder.INSTANCE.serverOutput=null;
            }
        } else {
            throw new IllegalStateException("No server running.");
        }
    }
    
    public static void writeLine(String line) {
        Holder.INSTANCE.serverInput.println(line);
        Holder.INSTANCE.serverInput.flush();
    }
    
    public static String readLine() {
        try{
            return Holder.INSTANCE.serverOutput.readLine();
        } catch(IOException e) {
            shutdownServer();
            return null;
        }
    }
    
    public static boolean serverRunning() {
        return Holder.INSTANCE.serverInstance!=null;
    }
}
