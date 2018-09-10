package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class RootStep extends Step {

    private final Keyword keyword;

    public RootStep(Position position, Keyword keyword, String text, List<Step> substeps, Optional<DocString> docString, Optional<DataTable> dataTable) {
        super(position, text, substeps, docString, dataTable);
        this.keyword = keyword;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    @Override
    public String toString() {
        return keyword + " " + getText();
    }
}
