package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class Background implements Positioned, Described {

    private final Position position;
    private final Optional<String> description;
    private final List<RootStep> steps;

    public Background(Position position, Optional<String> description, List<RootStep> steps) {
        this.position = position;
        this.description = description;
        this.steps = steps;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Optional<String> getDescription() {
        return description;
    }

    public List<RootStep> getSteps() {
        return steps;
    }
}
