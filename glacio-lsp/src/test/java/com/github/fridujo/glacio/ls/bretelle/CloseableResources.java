package com.github.fridujo.glacio.ls.bretelle;

import java.io.Closeable;
import java.util.List;

class CloseableResources implements Closeable {
    private final List<ThrowingRunnable> toClose;

    CloseableResources(List<ThrowingRunnable> toClose) {
        this.toClose = toClose;
    }

    @Override
    public void close() {
        for (ThrowingRunnable closeableResource : toClose) {
            try {
                closeableResource.run();
            } catch (Exception e) {
                // eat this
            }
        }
    }
}
