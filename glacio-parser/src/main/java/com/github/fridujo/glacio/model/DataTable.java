package com.github.fridujo.glacio.model;

import java.util.List;

public class DataTable implements StepArgument {
    private final List<Row> rows;
    private final int minWidth;
    private final int maxWidth;

    public DataTable(List<Row> rows) {
        this.rows = rows;
        minWidth = rows.stream().mapToInt(Row::length).min().orElse(0);
        maxWidth = rows.stream().mapToInt(Row::length).max().orElse(0);
    }

    @Override
    public Type getType() {
        return Type.DATA_TABLE;
    }

    public List<Row> getRows() {
        return rows;
    }

    public boolean isSquare() {
        return minWidth == maxWidth;
    }

    public boolean hasWidth(int width) {
        return width <= minWidth;
    }

    public static class Row {
        private final List<String> cells;

        public Row(List<String> cells) {
            this.cells = cells;
        }

        public List<String> getCells() {
            return cells;
        }

        public int length() {
            return cells.size();
        }
    }
}
