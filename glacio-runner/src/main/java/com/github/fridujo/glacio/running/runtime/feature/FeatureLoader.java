package com.github.fridujo.glacio.running.runtime.feature;

import java.util.Set;

import com.github.fridujo.glacio.model.Feature;

public interface FeatureLoader {

    Set<Feature> load(Set<String> paths);
}
