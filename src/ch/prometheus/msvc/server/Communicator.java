/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 *
 * @author Denahiro
 */
public class Communicator implements Runnable{
    
    private PrintWriter toServer;
    private BufferedReader fromServer;
    
    private PrintListener output;

    public Communicator(PrintListener out,Process server) {
        this.output=out;
        this.toServer=new PrintWriter(server.getOutputStream());
        this.fromServer=new BufferedReader(new InputStreamReader(server.getErrorStream()));
    }
    
    public void println(String in) {
        this.output.println(in);
        this.toServer.println(in);
        this.toServer.flush();
    }
    
    @Override
    public void run(){
        try {
            while(!Thread.interrupted()) {
                this.output.println(this.fromServer.readLine());
            }
            this.fromServer.close();
        } catch (IOException e) {
            Logger.getGlobal().throwing(this.getClass().getCanonicalName(), "run()", e);
        } finally {
            this.fromServer=null;
            this.toServer.close();
            this.toServer=null;
            this.output=null;
        }
    }
}
