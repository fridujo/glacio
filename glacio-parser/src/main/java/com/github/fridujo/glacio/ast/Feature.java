package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class Feature implements Positioned, Described, Visitable {

    private final Position startPosition;
    private final Position endPosition;
    private final String name;
    private final Optional<PositionedString> language;
    private final List<Tag> tags;
    private final Optional<String> description;
    private final Optional<Background> background;
    private final List<Scenario> scenarios;

    public Feature(Position startPosition,
                   Position endPosition,
                   String name,
                   Optional<PositionedString> language,
                   List<Tag> tags,
                   Optional<String> description,
                   Optional<Background> background,
                   List<Scenario> scenarios) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.name = name;
        this.language = language;
        this.tags = tags;
        this.description = description;
        this.background = background;
        this.scenarios = scenarios;
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

    public Optional<PositionedString> getLanguage() {
        return language;
    }

    public List<Tag> getTags() {
        return tags;
    }

    @Override
    public Optional<String> getDescription() {
        return description;
    }

    public Optional<Background> getBackground() {
        return background;
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitFeature(this);
        // Not very conventional, need a domain specific type instead on the generic PositionedString
        language.ifPresent(visitor::visitLanguage);
        // TODO positioned language hint, name, tag, description
        background.ifPresent(b -> b.accept(visitor));
        scenarios.forEach(s -> s.accept(visitor));
    }
}
