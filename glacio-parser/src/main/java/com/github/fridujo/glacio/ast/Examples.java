package com.github.fridujo.glacio.ast;

import java.util.List;

public class Examples implements Positioned {
    private final Position position;
    private final TableRow header;
    private final List<TableRow> body;

    public Examples(Position position, TableRow header, List<TableRow> body) {
        this.position = position;
        this.header = header;
        this.body = body;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public TableRow getHeader() {
        return header;
    }

    public List<TableRow> getBody() {
        return body;
    }
}
