package com.github.fridujo.glacio.parsing.i18n;

import static java.util.Collections.emptySet;

import java.util.Set;

public class LanguageKeywords {

    private final String code;
    private final String languageName;
    private final String nativeName;
    private final Set<String> feature;
    private final Set<String> background;
    private final Set<String> scenarioOutline;
    private final Set<String> scenario;
    private final Set<String> given;
    private final Set<String> when;
    private final Set<String> then;
    private final Set<String> and;
    private final Set<String> examples;

    public LanguageKeywords(String code,
                            String languageName,
                            String nativeName,
                            Set<String> feature,
                            Set<String> background,
                            Set<String> scenarioOutline,
                            Set<String> scenario,
                            Set<String> given,
                            Set<String> when,
                            Set<String> then,
                            Set<String> and, Set<String> examples) {
        this.code = code;
        this.languageName = languageName;
        this.nativeName = nativeName;
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
            "empty",
            "empty",
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

    public String getCode() {
        return code;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getNativeName() {
        return nativeName;
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
