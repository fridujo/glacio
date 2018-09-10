package com.github.fridujo.glacio.junit.engine.descriptor;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.model.Feature;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;

import java.util.Set;

public class GlacioEngineDescriptor extends EngineDescriptor implements Node<GlacioEngineExecutionContext> {

    public GlacioEngineDescriptor(UniqueId uniqueId, Set<Feature> features) {
        super(uniqueId, "Glacio");
        features.forEach(feature -> addChild(new FeatureDescriptor(getUniqueId(), feature)));
    }

    @Override
    public GlacioEngineExecutionContext prepare(GlacioEngineExecutionContext context) {
        context.getEventAware().beforeAllFeatures();
        return context;
    }

    @Override
    public void cleanUp(GlacioEngineExecutionContext context) {
        context.getEventAware().afterAllFeatures();
    }
}
