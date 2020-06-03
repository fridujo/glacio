package com.github.fridujo.glacio.ls.bretelle;

import java.io.IOException;
import java.io.UncheckedIOException;

@FunctionalInterface
public interface ThrowingRunnable {

    static Runnable silence(ThrowingRunnable throwingRunnable) {
        return () -> {
            try {
                throwingRunnable.run();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else if (e instanceof IOException) {
                    throw new UncheckedIOException((IOException) e);
                } else {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    void run() throws Exception;
}
