package com.github.fridujo.glacio.junit.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.LauncherDiscoveryRequest;

class DiscoveryTests {

    private final GlacioTestEngine engine = new GlacioTestEngine();
    private final UniqueId engineId = UniqueId.forEngine(engine.getId());

    @Test
    void disabled_engine_discovers_no_features() {
        LauncherDiscoveryRequest request = request()
            // Already declared in junit-platform.properties, here to clarify intention
            .configurationParameter("glacio-junit-engine.disabled", "true")
            .build();

        TestDescriptor testDescriptor = engine.discover(request, engineId);

        assertThat(testDescriptor.getChildren()).isEmpty();
    }

    @Test
    void default_discovery() {
        LauncherDiscoveryRequest request = request()
            .configurationParameter("glacio-junit-engine.disabled", "false")
            .build();

        TestDescriptor testDescriptor = engine.discover(request, engineId);

        assertThat(testDescriptor.getChildren())
            .hasSize(2)
            .extracting(t -> t.getDisplayName())
            .containsExactlyInAnyOrder(
                "Feature: testing colors",
                "Feature: feature execution"
            );
    }

    @Test
    void discovery_with_specified_paths() {
        LauncherDiscoveryRequest request = request()
            .configurationParameter("glacio-junit-engine.disabled", "false")
            .configurationParameter("glacio-junit-engine.featurePaths", "classpath:features/exec")
            .build();

        TestDescriptor testDescriptor = engine.discover(request, engineId);

        assertThat(testDescriptor.getChildren()).hasSize(1);

        TestDescriptor featureDescriptor = testDescriptor.getChildren().iterator().next();

        assertThat(featureDescriptor.getChildren()).hasSize(1);

        TestDescriptor exampleDescriptor = featureDescriptor.getChildren().iterator().next();

        assertThat(exampleDescriptor.getChildren()).hasSize(3);
        assertThat(exampleDescriptor.getDescendants()).hasSize(5);
    }
}
