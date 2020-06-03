package com.github.fridujo.glacio.ast;

import java.util.List;

public class Examples implements Positioned {
    private final Position startPosition;
    private final Position endPosition;
    private final TableRow header;
    private final List<TableRow> body;

    public Examples(Position startPosition, Position endPosition, TableRow header, List<TableRow> body) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.header = header;
        this.body = body;
    }

    @Override
    public Position startPosition() {
        return startPosition;
    }

    @Override
    public Position endPosition() {
        return endPosition;
    }

    public TableRow getHeader() {
        return header;
    }

    public List<TableRow> getBody() {
        return body;
    }
}
