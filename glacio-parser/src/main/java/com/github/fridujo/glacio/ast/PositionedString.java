package com.github.fridujo.glacio.ast;

public class PositionedString implements Positioned {
    private final Position position;
    private final String value;

    public PositionedString(Position position, String value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public String getValue() {
        return value;
    }
}
