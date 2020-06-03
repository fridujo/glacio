package com.github.fridujo.glacio.ast;

import java.util.List;

public class DataTable implements Positioned, Visitable {

    private final Position startPosition;
    private final Position endPosition;
    private final List<TableRow> rows;

    public DataTable(Position startPosition, Position endPosition, List<TableRow> rows) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.rows = rows;
    }

    @Override
    public Position startPosition() {
        return startPosition;
    }

    @Override
    public Position endPosition() {
        return endPosition;
    }

    public List<TableRow> getRows() {
        return rows;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDataTable(this);
    }
}
