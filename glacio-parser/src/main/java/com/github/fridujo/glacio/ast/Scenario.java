package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class Scenario implements Positioned, Described {

    private final Position position;
    private final String name;
    private final List<Tag> tags;
    private final Optional<String> description;
    private final List<RootStep> steps;

    public Scenario(Position position, String name, List<Tag> tags, Optional<String> description, List<RootStep> steps) {
        this.position = position;
        this.name = name;
        this.tags = tags;
        this.description = description;
        this.steps = steps;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public Optional<String> getDescription() {
        return description;
    }

    public List<RootStep> getSteps() {
        return steps;
    }
}
