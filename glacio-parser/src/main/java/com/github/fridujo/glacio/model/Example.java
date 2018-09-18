package com.github.fridujo.glacio.model;

import java.util.List;
import java.util.Map;

public class Example {
    private final String name;
    private final Map<String, String> parameters;
    private final List<Step> steps;

    public Example(String name, Map<String, String> parameters, List<Step> steps) {
        this.name = name;
        this.parameters = parameters;
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public List<Step> getSteps() {
        return steps;
    }
}
