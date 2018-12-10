package com.github.fridujo.glacio.junit.engine.descriptor;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;

abstract class AbstractGlacioTestDescriptor extends AbstractTestDescriptor implements Node<GlacioEngineExecutionContext> {

    protected AbstractGlacioTestDescriptor(UniqueId uniqueId, String displayName) {
        super(uniqueId, displayName);
    }
}
