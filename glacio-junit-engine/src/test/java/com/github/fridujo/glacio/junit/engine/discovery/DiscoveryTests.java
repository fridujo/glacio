package com.github.fridujo.glacio.junit.engine.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.LauncherDiscoveryRequest;

import com.github.fridujo.glacio.junit.engine.GlacioTestEngine;
import com.github.fridujo.glacio.running.api.configuration.GlacioConfiguration;

class DiscoveryTests {

    private final GlacioTestEngine engine = new GlacioTestEngine();
    private final UniqueId engineId = UniqueId.forEngine(engine.getId());

    @Test
    void disabled_engine_discovers_no_features() {
        LauncherDiscoveryRequest request = request()
            // Already declared in junit-platform.properties, here to clarify intention
            .configurationParameter("glacio-junit-engine.disabled", "true")
            .build();

        TestDescriptor glacioDescriptor = engine.discover(request, engineId);

        assertThat(glacioDescriptor.getChildren())
            .as("configuration descriptors")
            .isEmpty();
    }

    @Test
    void discovery_without_selectors() {
        LauncherDiscoveryRequest request = request()
            .configurationParameter("glacio-junit-engine.disabled", "false")
            .build();

        TestDescriptor glacioDescriptor = engine.discover(request, engineId);

        assertThat(glacioDescriptor.getChildren())
            .as("configuration descriptors")
            .isEmpty();
    }

    @Test
    void discovery_with_selector() {
        LauncherDiscoveryRequest request = request()
            .configurationParameter("glacio-junit-engine.disabled", "false")
            .selectors(DiscoverySelectors.selectClass(TestInitializer.class.getName()))
            .build();

        TestDescriptor glacioDescriptor = engine.discover(request, engineId);

        assertThat(glacioDescriptor.getChildren()).hasSize(1);

        TestDescriptor configurationDescriptor = glacioDescriptor.getChildren().iterator().next();

        assertThat(configurationDescriptor.getChildren()).hasSize(1);

        TestDescriptor featureDescriptor = configurationDescriptor.getChildren().iterator().next();
        assertThat(featureDescriptor.getChildren()).hasSize(1);

        TestDescriptor scenarioDescriptor = featureDescriptor.getChildren().iterator().next();

        assertThat(scenarioDescriptor.getChildren()).hasSize(3);
        assertThat(scenarioDescriptor.getDescendants()).hasSize(5);
    }

    @GlacioConfiguration(featurePaths = "classpath:features/exec")
    public static class TestInitializer {
    }
}
