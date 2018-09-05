package com.github.fridujo.glacio.parsing.i18n;

import java.util.Set;

import static java.util.Collections.emptySet;

public class LanguageKeywords {

    private final String languageName;
    private final Set<String> feature;
    private final Set<String> background;
    private final Set<String> scenarioOutline;
    private final Set<String> scenario;
    private final Set<String> given;
    private final Set<String> when;
    private final Set<String> then;
    private final Set<String> and;
    private final Set<String> examples;

    public LanguageKeywords(String languageName,
                            Set<String> feature,
                            Set<String> background,
                            Set<String> scenarioOutline,
                            Set<String> scenario,
                            Set<String> given,
                            Set<String> when,
                            Set<String> then,
                            Set<String> and, Set<String> examples) {
        this.languageName = languageName;
        this.feature = feature;
        this.background = background;
        this.scenarioOutline = scenarioOutline;
        this.scenario = scenario;
        this.given = given;
        this.when = when;
        this.then = then;
        this.and = and;
        this.examples = examples;
    }

    public static LanguageKeywords empty() {
        return new LanguageKeywords("empty",
            emptySet(),
            emptySet(),
            emptySet(),
            emptySet(),
            emptySet(),
            emptySet(),
            emptySet(),
            emptySet(),
            emptySet());
    }

    public String getLanguageName() {
        return languageName;
    }

    public Set<String> getFeature() {
        return feature;
    }

    public Set<String> getBackground() {
        return background;
    }

    public Set<String> getScenarioOutline() {
        return scenarioOutline;
    }

    public Set<String> getScenario() {
        return scenario;
    }

    public Set<String> getGiven() {
        return given;
    }

    public Set<String> getWhen() {
        return when;
    }

    public Set<String> getThen() {
        return then;
    }

    public Set<String> getAnd() {
        return and;
    }

    public Set<String> getExamples() {
        return examples;
    }
}
