package com.github.fridujo.glacio.parsing.lexer;

import com.github.fridujo.glacio.parsing.charstream.Position;

import java.util.Arrays;
import java.util.Objects;

public class Token {
    private final TokenType type;
    private final String literal;
    private final Position position;

    public Token(TokenType type, String literal, Position position) {
        this.type = type;
        this.literal = literal;
        this.position = position;
    }

    public Token(TokenType type, Position position) {
        this(type, "", position);
    }

    public Token(FixedTokenDefinition fixedTokenDefinition, Position position) {
        this(fixedTokenDefinition.getType(), fixedTokenDefinition.getLiteralString(), position);
    }

    public TokenType getType() {
        return type;
    }

    public String getLiteral() {
        return literal;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Token{" +
            "type=" + type +
            ", literal='" + literal.replace("\n", "") + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.type &&
            Objects.equals(literal, token.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, literal);
    }

    public boolean isOfType(TokenType tokenType) {
        return isOfAnyType(tokenType);
    }

    public boolean isOfAnyType(TokenType... tokenTypes) {
        return Arrays.stream(tokenTypes)
            .map(tt -> tt == getType())
            .mapToInt(sameType -> sameType ? 1 : 0)
            .sum() > 0;
    }
}
