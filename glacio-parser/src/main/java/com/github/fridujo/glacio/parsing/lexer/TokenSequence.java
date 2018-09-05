package com.github.fridujo.glacio.parsing.lexer;

import com.github.fridujo.glacio.parsing.charstream.Position;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TokenSequence {
    private final Position position;
    private final List<Token> tokens;

    public TokenSequence(Position position, List<Token> tokens) {
        this.position = position;
        this.tokens = tokens;
    }

    public String toLiteral() {
        return tokens.stream().map(Token::getLiteral).collect(Collectors.joining());
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenSequence that = (TokenSequence) o;
        return Objects.equals(tokens, that.tokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokens);
    }

    @Override
    public String toString() {
        return tokens.toString();
    }
}
