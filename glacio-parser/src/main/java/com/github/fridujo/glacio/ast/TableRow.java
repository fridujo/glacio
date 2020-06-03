package com.github.fridujo.glacio.ast;

import java.util.List;

public class TableRow implements Positioned {
    private final Position startPosition;
    private final Position endPosition;
    private final List<TableCell> cells;

    public TableRow(Position startPosition, Position endPosition, List<TableCell> cells) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.cells = cells;
    }

    public List<TableCell> getCells() {
        return cells;
    }

    @Override
    public Position startPosition() {
        return startPosition;
    }

    @Override
    public Position endPosition() {
        return endPosition;
    }
}
