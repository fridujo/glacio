package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class Step implements Positioned, Visitable {

    private final Position startPosition;
    private final Position endPosition;
    private final Optional<Keyword> keyword;
    private final String text;
    private final List<Step> substeps;
    private final Optional<DocString> docString;
    private final Optional<DataTable> dataTable;

    public Step(Position startPosition, Position endPosition, Optional<Keyword> keyword, String text, List<Step> substeps, Optional<DocString> docString, Optional<DataTable> dataTable) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.keyword = keyword;
        this.text = text;
        this.docString = docString;
        this.dataTable = dataTable;
        this.substeps = substeps;
    }

    @Override
    public Position startPosition() {
        return startPosition;
    }

    @Override
    public Position endPosition() {
        return endPosition;
    }

    public Optional<Keyword> getKeyword() {
        return keyword;
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
        return keyword.map(s -> s + " ").orElse("") + text;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitStep(this);
        docString.ifPresent(ds -> ds.accept(visitor));
        dataTable.ifPresent(dt -> dt.accept(visitor));
        substeps.forEach(s -> s.accept(visitor));
    }
}
