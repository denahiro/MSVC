/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server.files;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author stko
 */
public class ServerDataTest {

    private ServerData data;
    private File local;

    @Before
    public void setUp() {
        data = new ServerData();
        local = new File("tmptest");
    }

    @After
    public void tearDown() {
        data.delete();
        local.delete();
    }

    @Test
    public void testCreate() throws IOException {
        data.create(local);
        assertTrue(new File(local, "server.properties").exists());
        assertTrue(new File(local, data.getName()).exists());
    }

    @Test
    public void testDelete() throws IOException {
        data.create(local);
        data.delete();
        assertFalse(new File(local, "server.properties").exists());
        assertFalse(new File(local, data.getName()).exists());
    }
}
