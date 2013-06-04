/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author stko
 */
public class TestZipper {

    private static final File basePath = new File("tmpTest");
    private Callable<Boolean> zipper;
    private Callable<Boolean> unzipper;
    private File destination;
    private File zipTarget;
    private Collection<String> files = new ArrayList<>();
    private Collection<File> sources = new ArrayList<>();

    public TestZipper() {
    }

    @Before
    public void setUp() throws FileNotFoundException, IOException {
        zipTarget = new File(basePath, "target.zip");
        zipTarget.getParentFile().mkdirs();
        destination = new File(basePath, "unzipdir");
        destination.mkdirs();
        zipper = new AutocloseDecorator(new Zipper(zipTarget, sources, basePath));
        files.add("simple.txt");
        files.add("dir/dirSimple.txt");
        files.add("dir/dir2/dirRecursive.txt");
        for (String s : files) {
            File f = new File(basePath, s);
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        sources.add(new File(basePath, "simple.txt"));
        sources.add(new File(basePath, "dir"));
    }

    @After
    public void tearDown() throws IOException {
        deleteRecursive(basePath);
    }

    @Test
    public void testZipping() throws Exception {
        zipper.call();
        unzipper = new AutocloseDecorator(new Unzipper(zipTarget, destination));
        unzipper.call();
        for (String s : files) {
            File f = new File(destination, s);
            assertTrue(f.isFile());
        }
    }

    private void deleteRecursive(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                deleteRecursive(f);
            } else {
                f.delete();
            }
        }
        dir.delete();
    }
}
