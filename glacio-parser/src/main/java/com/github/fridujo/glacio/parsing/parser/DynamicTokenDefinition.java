package com.github.fridujo.glacio.parsing.parser;

import com.github.fridujo.glacio.parsing.lexer.TokenType;

import java.util.Set;

public class DynamicTokenDefinition {
    private final TokenType type;
    private final Set<String> literals;

    private DynamicTokenDefinition(TokenType type, Set<String> literals) {
        this.type = type;
        this.literals = literals;
    }

    public static DynamicTokenDefinition dynamicToken(TokenType type, Set<String> literals) {
        return new DynamicTokenDefinition(type, literals);
    }

    public TokenType getType() {
        return type;
    }

    public Set<String> getLiterals() {
        return literals;
    }
}
