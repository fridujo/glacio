package com.github.fridujo.glacio.ast;

import java.util.Objects;

public class Position {
    private final int line;
    private final int column;

    public Position(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public static Position from(com.github.fridujo.glacio.parsing.charstream.Position position) {
        return new Position(position.getLine(), position.getColumn());
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public com.github.fridujo.glacio.parsing.charstream.Position asParsingPosition() {
        return new com.github.fridujo.glacio.parsing.charstream.Position(line, column);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return line == position.line &&
            column == position.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, column);
    }

    @Override
    public String toString() {
        return "Position{" +
            "line=" + line +
            ", column=" + column +
            '}';
    }

    public boolean isIn(Positioned positioned) {
        return (
            line == positioned.startPosition().line && column >= positioned.startPosition().column
                || line > positioned.startPosition().line)
            && (
            line == positioned.endPosition().line && column <= positioned.endPosition().column
                || line < positioned.endPosition().line
        );
    }
}
