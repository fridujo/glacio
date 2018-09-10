package com.github.fridujo.glacio.running.runtime.feature;

import com.github.fridujo.glacio.model.Feature;

import java.util.Set;

public interface FeatureLoader {

    Set<Feature> load(Set<String> paths);
}
