package com.github.fridujo.glacio.running.logging;

import org.slf4j.LoggerFactory;

public class Slf4jLogger implements Logger {

    private final org.slf4j.Logger delegate;

    public Slf4jLogger(Class<?> origin) {
        delegate = LoggerFactory.getLogger(origin);
    }

    @Override
    public void info(String message) {
        delegate.info(message);
    }

    @Override
    public void warn(String message, Exception cause) {
        delegate.warn(message, cause);
    }
}
