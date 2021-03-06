package com.github.fridujo.glacio.running;

public class GlacioRunnerException extends RuntimeException {

    public GlacioRunnerException(String message) {
        super(message);
    }

    public GlacioRunnerException(Exception cause) {
        super(cause);
    }

    public GlacioRunnerException(String message, Exception cause) {
        super(message, cause);
    }
}
