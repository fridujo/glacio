package com.github.fridujo.glacio.running.runtime;

public class GlacioRunnerInitializationException extends RuntimeException {

    public GlacioRunnerInitializationException(String message, Exception cause) {
        super(message, cause);
    }

    public GlacioRunnerInitializationException(String message) {
        super(message);
    }
}
