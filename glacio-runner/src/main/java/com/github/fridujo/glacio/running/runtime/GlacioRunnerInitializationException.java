package com.github.fridujo.glacio.running.runtime;

import com.github.fridujo.glacio.parsing.ParsingException;

public class GlacioRunnerInitializationException extends RuntimeException {

    public GlacioRunnerInitializationException(String message, ParsingException cause) {
        super(message, cause);
    }
}
