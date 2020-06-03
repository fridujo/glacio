package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class Scenario implements Positioned, Described, Visitable {

    private final Position startPosition;
    private final Position endPosition;
    private final String name;
    private final List<Tag> tags;
    private final Optional<String> description;
    private final List<Step> steps;

    public Scenario(Position startPosition, Position endPosition, String name, List<Tag> tags, Optional<String> description, List<Step> steps) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.name = name;
        this.tags = tags;
        this.description = description;
        this.steps = steps;
    }

    @Override
    public Position startPosition() {
        return startPosition;
    }

    @Override
    public Position endPosition() {
        return endPosition;
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

    public List<Step> getSteps() {
        return steps;
    }

    @Override
    public void accept(Visitor visitor) {
        visitScenario(visitor);
        steps.forEach(s -> s.accept(visitor));
    }

    protected void visitScenario(Visitor visitor) {
        visitor.visitScenario(this);
    }
}
