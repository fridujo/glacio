package com.github.fridujo.glacio.junit.engine.descriptor;

import java.util.Set;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.model.Feature;

public class GlacioEngineDescriptor extends EngineDescriptor implements Node<GlacioEngineExecutionContext> {

    public GlacioEngineDescriptor(UniqueId uniqueId, Set<Feature> features) {
        super(uniqueId, "Glacio");
        features.forEach(feature -> addChild(new FeatureDescriptor(getUniqueId(), feature)));
    }
}
