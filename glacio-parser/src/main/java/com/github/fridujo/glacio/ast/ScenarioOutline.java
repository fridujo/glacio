package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class ScenarioOutline extends Scenario {
    private final Examples examples;

    public ScenarioOutline(Position position, String name, List<Tag> tags, Optional<String> description, List<RootStep> steps, Examples examples) {
        super(position, name, tags, description, steps);
        this.examples = examples;
    }

    public Examples getExamples() {
        return examples;
    }
}
