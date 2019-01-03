package com.github.fridujo.glacio.running.logging;

public interface Logger {

    void info(String message);

    void warn(String message, Exception cause);
}
