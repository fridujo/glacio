package com.github.fridujo.glacio.ast;

import java.util.List;
import java.util.Optional;

public class Feature implements Positioned, Described {

    private final Position position;
    private final String name;
    private final Optional<PositionedString> language;
    private final List<Tag> tags;
    private final Optional<String> description;
    private final Optional<Background> background;
    private final List<Scenario> scenarios;

    public Feature(Position position,
                   String name,
                   Optional<PositionedString> language,
                   List<Tag> tags,
                   Optional<String> description,
                   Optional<Background> background,
                   List<Scenario> scenarios) {
        this.position = position;
        this.name = name;
        this.language = language;
        this.tags = tags;
        this.description = description;
        this.background = background;
        this.scenarios = scenarios;
    }

    @Override
    public Position getPosition() {
        return position;
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
}
