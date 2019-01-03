package com.github.fridujo.glacio.running.logging;

import java.util.logging.Level;

public class JulLogger implements Logger {

    private final java.util.logging.Logger julLogger;

    public JulLogger(Class<?> origin) {
        julLogger = java.util.logging.Logger.getLogger(origin.getName());
    }

    @Override
    public void info(String message) {
        julLogger.log(Level.INFO, message);
    }

    @Override
    public void warn(String message, Exception cause) {
        julLogger.log(Level.WARNING, cause, () -> message);
    }
}
