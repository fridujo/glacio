package com.github.fridujo.glacio.junit.engine;

import java.util.Optional;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;

import com.github.fridujo.glacio.junit.engine.descriptor.GlacioEngineDescriptor;
import com.github.fridujo.glacio.junit.engine.discovery.DiscoverySelectorResolver;

public class GlacioTestEngine extends HierarchicalTestEngine<GlacioEngineExecutionContext> {

    @Override
    public String getId() {
        return GlacioProperties.ENGINE_ID;
    }

    public Optional<String> getGroupId() {
        return Optional.of(GlacioProperties.GROUP_ID);
    }

    public Optional<String> getArtifactId() {
        return Optional.of(GlacioProperties.ARTIFACT_ID);
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        TestDescriptor testDescriptor = new GlacioEngineDescriptor(uniqueId);

        new DiscoverySelectorResolver(discoveryRequest).resolveFor(testDescriptor);

        return testDescriptor;
    }

    @Override
    protected GlacioEngineExecutionContext createExecutionContext(ExecutionRequest executionRequest) {
        return new GlacioEngineExecutionContext();
    }
}
