package com.github.fridujo.glacio.running.runtime;

import com.github.fridujo.glacio.running.GlacioRunnerException;

public class GlacioRunnerInitializationException extends GlacioRunnerException {
    public GlacioRunnerInitializationException(String message) {
        super(message);
    }

    public GlacioRunnerInitializationException(String message, Exception cause) {
        super(message, cause);
    }
}
