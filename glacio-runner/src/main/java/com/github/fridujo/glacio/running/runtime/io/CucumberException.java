package com.github.fridujo.glacio.running.runtime.io;

public class CucumberException extends RuntimeException {
    public CucumberException(Exception cause) {
        super(cause);
    }

    public CucumberException(String message) {
        super(message);
    }
}
