package com.github.fridujo.glacio.junit.engine.descriptor;

import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.model.Feature;

public class FeatureDescriptor extends AbstractGlacioTestDescriptor {

    public FeatureDescriptor(UniqueId configurationId, UniqueId parentUniqueId, Feature feature) {
        super(configurationId, parentUniqueId.append("feature", feature.getName()), "Feature: " + feature.getName());

        feature.getExamples().forEach(example -> addChild(new ExampleDescriptor(configurationId, getUniqueId(), example)));
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }
}
