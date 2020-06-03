package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class Background implements Positioned, Described, Visitable {

    private final Position startPosition;
    private final Position endPosition;
    private final Optional<String> description;
    private final List<Step> steps;

    public Background(Position startPosition, Position endPosition, Optional<String> description, List<Step> steps) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
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

    @Override
    public Optional<String> getDescription() {
        return description;
    }

    public List<Step> getSteps() {
        return steps;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitBackground(this);
        steps.forEach(s -> s.accept(visitor));
    }
}
