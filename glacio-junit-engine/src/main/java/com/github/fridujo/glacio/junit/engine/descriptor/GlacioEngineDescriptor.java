package com.github.fridujo.glacio.junit.engine.descriptor;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;

public class GlacioEngineDescriptor extends EngineDescriptor implements Node<GlacioEngineExecutionContext> {

    public GlacioEngineDescriptor(UniqueId uniqueId) {
        super(uniqueId, "Glacio");
    }
}
