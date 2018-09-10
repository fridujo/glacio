package com.github.fridujo.glacio.running.runtime.io;

import com.github.fridujo.glacio.running.GlacioRunnerException;

import java.io.UncheckedIOException;

public class GlacioIOException extends GlacioRunnerException {
    public GlacioIOException(Exception cause) {
        super(cause);
    }

    public GlacioIOException(String message) {
        super(message);
    }

    public GlacioIOException(String message, UncheckedIOException cause) {
        super(message, cause);
    }
}
