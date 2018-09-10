package com.github.fridujo.glacio.junit.engine.descriptor;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.model.Feature;
import org.junit.platform.engine.UniqueId;

public class FeatureDescriptor extends GlacioTestDescriptor {

    public FeatureDescriptor(UniqueId parentUniqueId, Feature feature) {
        super(parentUniqueId.append("feature", feature.getName()), "Feature: " + feature.getName());

        feature.getExamples().forEach(example -> addChild(new ExampleDescriptor(getUniqueId(), example)));
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    @Override
    public GlacioEngineExecutionContext prepare(GlacioEngineExecutionContext context) {
        context.getEventAware().beforeFeature();
        return context;
    }

    @Override
    public void cleanUp(GlacioEngineExecutionContext context) {
        context.getEventAware().afterFeature();
    }
}
