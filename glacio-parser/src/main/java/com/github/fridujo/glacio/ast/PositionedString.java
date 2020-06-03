package com.github.fridujo.glacio.ast;

public class PositionedString implements Positioned {
    private final Position startPosition;
    private final Position endPosition;
    private final String value;

    public PositionedString(Position startPosition, Position endPosition, String value) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.value = value;
    }

    @Override
    public Position startPosition() {
        return startPosition;
    }

    @Override
    public Position endPosition() {
        return endPosition;
    }

    public String getValue() {
        return value;
    }
}
