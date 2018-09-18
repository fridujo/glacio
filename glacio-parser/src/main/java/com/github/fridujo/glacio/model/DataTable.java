package com.github.fridujo.glacio.model;

import java.util.List;

public class DataTable implements StepArgument {
    private final List<Row> rows;

    public DataTable(List<Row> rows) {
        this.rows = rows;
    }

    @Override
    public Type getType() {
        return Type.DATA_TABLE;
    }

    public List<Row> getRows() {
        return rows;
    }

    public static class Row {
        private final List<String> cells;

        public Row(List<String> cells) {
            this.cells = cells;
        }

        public List<String> getCells() {
            return cells;
        }
    }
}
