/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Denahiro
 */
public class ExecutorHolder {
    public static final ExecutorService EXECUTOR=Executors.newCachedThreadPool();
}
