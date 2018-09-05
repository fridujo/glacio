package com.github.fridujo.glacio.parsing;

import com.github.fridujo.glacio.parsing.charstream.Position;

public class ParsingException extends RuntimeException {
    public ParsingException(Position position, String message) {
        super(message + " at " + position);
    }
}
