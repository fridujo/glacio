package com.github.fridujo.glacio.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Example {
    private final String name;
    private final Map<String, String> parameters;
    private final List<Step> steps;
    private final Set<String> tags;

    public Example(String name, Map<String, String> parameters, List<Step> steps, Set<String> tags) {
        this.name = name;
        this.parameters = parameters;
        this.steps = steps;
        this.tags = tags;
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

    public Set<String> getTags() {
        return tags;
    }
}
