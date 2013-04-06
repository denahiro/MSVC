/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server.files;

import java.io.File;

/**
 *
 * @author Denahiro
 */
public class ServerData{
    private static File remoteLocation;
    
    public final String name;

    public ServerData(String name) {
        this.name = name;
    }

    public void load() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static void setRemoteLocation(File dir) {
        ServerData.remoteLocation=dir;
    }
}
