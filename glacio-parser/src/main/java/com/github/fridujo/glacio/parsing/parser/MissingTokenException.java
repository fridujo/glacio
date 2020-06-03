package com.github.fridujo.glacio.parsing.parser;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.github.fridujo.glacio.parsing.ParsingException;
import com.github.fridujo.glacio.parsing.lexer.FixedTokenDefinition;
import com.github.fridujo.glacio.parsing.lexer.Token;

public class MissingTokenException extends ParsingException {
    
    private final List<DynamicTokenDefinition> expectedTokens;

    public MissingTokenException(FixedTokenDefinition expected, Token found) {
        super(found.getPosition(), "Expecting " + expected.getType().name() + " token (" + characterWording(expected) + " '" + expected.getLiteralString() + "'), but found " + found);
        expectedTokens = emptyList();
    }

    public MissingTokenException(Token found, DynamicTokenDefinition... expectations) {
        super(found.getPosition(), "Expecting " + Arrays
            .stream(expectations)
            .map(MissingTokenException::wording)
            .collect(Collectors.joining(" or "))
            + ", but found " + found);
        expectedTokens = Arrays.asList(expectations);
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

    public List<DynamicTokenDefinition> getExpectedTokens() {
        return expectedTokens;
    }
}
