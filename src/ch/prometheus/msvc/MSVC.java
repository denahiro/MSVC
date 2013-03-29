/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc;

import ch.prometheus.msvc.server.ServerHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Denahiro
 */
public class MSVC {
    public static void main(String[] args) throws IOException, InterruptedException{
        ServerHandler.launchServer();
        BufferedReader inputReader=new BufferedReader(new InputStreamReader(System.in));
        while(!inputReader.readLine().equals("q")) {
            System.out.println(ServerHandler.readLine());
        }
        ServerHandler.shutdownServer();
    }
}
