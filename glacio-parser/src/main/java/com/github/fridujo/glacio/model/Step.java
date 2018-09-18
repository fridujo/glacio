package com.github.fridujo.glacio.model;

import java.util.List;
import java.util.Optional;

public class Step {

    private final boolean background;
    private final Optional<Keyword> keyword;
    private final String text;
    private final Optional<StepArgument> argument;
    private final List<Step> substeps;

    public Step(boolean background, Optional<Keyword> keyword, String text, Optional<StepArgument> argument, List<Step> substeps) {
        this.background = background;
        this.keyword = keyword;
        this.text = text;
        this.argument = argument;
        this.substeps = substeps;
    }

    public boolean isBackground() {
        return background;
    }

    public Optional<Keyword> getKeyword() {
        return keyword;
    }

    public String getText() {
        return text;
    }

    public Optional<StepArgument> getArgument() {
        return argument;
    }

    public List<Step> getSubsteps() {
        return substeps;
    }

    public String getLine() {
        return keyword.map(Keyword::getLiteral).orElse("") + text + (background ? " (background)" : "");
    }
}
