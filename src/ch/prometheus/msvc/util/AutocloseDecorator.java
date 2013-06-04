/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.prometheus.msvc.util;

import java.util.concurrent.Callable;

/**
 *
 * @author stko
 */
public class AutocloseDecorator implements Callable<Boolean> {

    private final CloseableTask toClose;

    public AutocloseDecorator(CloseableTask toClose) {
        this.toClose = toClose;
    }

    @Override
    public Boolean call() throws Exception {
        Boolean out = false;
        try {
            out = toClose.call();
        } finally {
            toClose.close();
        }
        return out;
    }
}
