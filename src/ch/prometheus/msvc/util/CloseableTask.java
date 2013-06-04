/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util;

import java.io.Closeable;
import java.util.concurrent.Callable;

/**
 *
 * @author stko
 */
public abstract class CloseableTask implements Closeable, Callable<Boolean> {
}
