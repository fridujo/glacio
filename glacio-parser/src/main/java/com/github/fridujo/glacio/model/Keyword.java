package com.github.fridujo.glacio.model;

public class Keyword {

    private final String literal;
    private final Type type;

    public Keyword(String literal, Type type) {
        this.literal = literal;
        this.type = type;
    }

    public String getLiteral() {
        return literal;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        GIVEN, WHEN, THEN
    }
}
