package com.github.fridujo.glacio.ast;

public class Keyword {
    private final KeywordType type;
    private final String literal;

    public Keyword(KeywordType type, String literal) {
        this.type = type;
        this.literal = literal;
    }

    public KeywordType getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        return type + " (" + literal + ')';
    }
}
