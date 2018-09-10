package com.github.fridujo.glacio.junit.engine.descriptor;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;

public abstract class GlacioTestDescriptor extends AbstractTestDescriptor implements Node<GlacioEngineExecutionContext> {

    protected GlacioTestDescriptor(UniqueId uniqueId, String displayName) {
        super(uniqueId, displayName);
    }
}
