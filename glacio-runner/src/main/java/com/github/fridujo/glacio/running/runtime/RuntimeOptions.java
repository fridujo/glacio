package com.github.fridujo.glacio.running.runtime;

import java.util.Set;

public class RuntimeOptions {
    private final Set<String> featurePaths;
    private final Set<String> gluePaths;
    private final Set<Class<?>> configurationClasses;

    public RuntimeOptions(Set<String> featurePaths,
                          Set<String> gluePaths,
                          Set<Class<?>> configurationClasses) {
        this.featurePaths = featurePaths;
        this.gluePaths = gluePaths;
        this.configurationClasses = configurationClasses;
    }

    public Set<String> getFeaturePaths() {
        return featurePaths;
    }

    public Set<String> getGluePaths() {
        return gluePaths;
    }

    public Set<Class<?>> getConfigurationClasses() {
        return configurationClasses;
    }
}
