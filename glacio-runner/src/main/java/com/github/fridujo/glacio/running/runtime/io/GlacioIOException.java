package com.github.fridujo.glacio.running.runtime.io;

import com.github.fridujo.glacio.running.GlacioRunnerException;

public class GlacioIOException extends GlacioRunnerException {
    public GlacioIOException(Exception cause) {
        super(cause);
    }

    public GlacioIOException(String message) {
        super(message);
    }
}
