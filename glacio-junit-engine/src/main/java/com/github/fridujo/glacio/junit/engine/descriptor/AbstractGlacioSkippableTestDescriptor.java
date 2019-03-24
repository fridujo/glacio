package com.github.fridujo.glacio.junit.engine.descriptor;

import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;

abstract class AbstractGlacioSkippableTestDescriptor extends AbstractGlacioTestDescriptor {

    protected final UniqueId exampleId;

    protected AbstractGlacioSkippableTestDescriptor(UniqueId configurationId, UniqueId exampleId, UniqueId uniqueId, String displayName) {
        super(configurationId, uniqueId, displayName);
        this.exampleId = exampleId;
    }
    
    @Override
    public SkipResult shouldBeSkipped(GlacioEngineExecutionContext context) {
        return context.shouldBeSkipped(exampleId);
    }
}
