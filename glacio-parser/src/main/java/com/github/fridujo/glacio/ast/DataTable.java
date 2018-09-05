package com.github.fridujo.glacio.ast;

import java.util.List;

public class DataTable implements Positioned {

    private final Position position;
    private final List<TableRow> rows;

    public DataTable(Position position, List<TableRow> rows) {
        this.position = position;
        this.rows = rows;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public List<TableRow> getRows() {
        return rows;
    }
}
