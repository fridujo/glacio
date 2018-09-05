package com.github.fridujo.glacio.parsing.parser;

import com.github.fridujo.glacio.parsing.ParsingException;
import com.github.fridujo.glacio.parsing.lexer.FixedTokenDefinition;
import com.github.fridujo.glacio.parsing.lexer.Token;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MissingTokenException extends ParsingException {

    public MissingTokenException(FixedTokenDefinition expected, Token found) {
        super(found.getPosition(), "Expecting " + expected.getType().name() + " token (" + characterWording(expected) + " '" + expected.getLiteralString() + "'), but found " + found);
    }

    public MissingTokenException(Token found, DynamicTokenDefinition... expectations) {
        super(found.getPosition(), "Expecting " + Arrays
            .stream(expectations)
            .map(MissingTokenException::wording)
            .collect(Collectors.joining(" or "))
            + ", but found " + found);
    }

    private static String wording(DynamicTokenDefinition dynamicTokenDefinition) {
        return dynamicTokenDefinition.getType().name() + " token (any of: " + dynamicTokenDefinition.getLiterals() + ")";
    }

    private static String characterWording(FixedTokenDefinition fixedTokenDefinition) {
        final String wording;
        if (fixedTokenDefinition.getLiteralString().length() > 1) {
            wording = "character sequence";
        } else {
            wording = "character";
        }
        return wording;
    }
}
