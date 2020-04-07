package com.github.fridujo.glacio.model;

import java.net.URI;
import java.util.List;

public class Feature {
    private final URI sourceURI;
    private final String name;
    private final Language language;
    private final List<Example> examples;

    public Feature(URI sourceURI, String name, Language language, List<Example> examples) {
        this.sourceURI = sourceURI;
        this.name = name;
        this.language = language;
        this.examples = examples;
    }

    public URI getSourceURI() {
        return sourceURI;
    }

    public String getName() {
        return name;
    }

    public Language getLanguage() {
        return language;
    }

    public List<Example> getExamples() {
        return examples;
    }
}
