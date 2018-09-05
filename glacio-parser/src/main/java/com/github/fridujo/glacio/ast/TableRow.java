package com.github.fridujo.glacio.ast;

import java.util.List;

public class TableRow implements Positioned {
    private final Position position;
    private final List<TableCell> cells;

    public TableRow(Position position, List<TableCell> cells) {
        this.position = position;
        this.cells = cells;
    }

    public List<TableCell> getCells() {
        return cells;
    }

    @Override
    public Position getPosition() {
        return position;
    }
}
