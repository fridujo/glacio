package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class ScenarioOutline extends Scenario {
    private final Examples examples;

    public ScenarioOutline(Position startPosition, Position endPosition, String name, List<Tag> tags, Optional<String> description, List<Step> steps, Examples examples) {
        super(startPosition, endPosition, name, tags, description, steps);
        this.examples = examples;
    }

    public Examples getExamples() {
        return examples;
    }

    protected void visitScenario(Visitor visitor) {
        visitor.visitScenarioOutline(this);
    }
}
