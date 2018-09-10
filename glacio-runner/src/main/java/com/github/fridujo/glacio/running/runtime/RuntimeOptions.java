package com.github.fridujo.glacio.running.runtime;

import java.util.Set;

public class RuntimeOptions {
    private final Set<String> featurePaths;
    private final Set<String> gluePaths;


    public RuntimeOptions(Set<String> featurePaths, Set<String> gluePaths) {
        this.featurePaths = featurePaths;
        this.gluePaths = gluePaths;
    }

    public Set<String> getFeaturePaths() {
        return featurePaths;
    }

    public Set<String> getGluePaths() {
        return gluePaths;
    }
}
