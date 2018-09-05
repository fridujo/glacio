package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class Step implements Positioned {

    private final Position position;
    private final String text;
    private final List<Step> substeps;
    private final Optional<DocString> docString;
    private final Optional<DataTable> dataTable;

    public Step(Position position, String text, List<Step> substeps, Optional<DocString> docString, Optional<DataTable> dataTable) {
        this.position = position;
        this.text = text;
        this.docString = docString;
        this.dataTable = dataTable;
        this.substeps = substeps;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    public List<Step> getSubsteps() {
        return substeps;
    }

    public Optional<DocString> getDocString() {
        return docString;
    }

    public Optional<DataTable> getDataTable() {
        return dataTable;
    }

    @Override
    public String toString() {
        return text;
    }
}
