package com.github.fridujo.glacio.junit.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;

import com.github.fridujo.glacio.junit.engine.descriptor.GlacioEngineDescriptor;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.running.runtime.Runtime;
import com.github.fridujo.glacio.running.runtime.feature.FeatureLoader;
import com.github.fridujo.glacio.running.runtime.feature.FileFeatureLoader;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;
import com.github.fridujo.glacio.running.runtime.glue.GlueFactory;
import com.github.fridujo.glacio.running.runtime.glue.JavaExecutableLookup;
import com.github.fridujo.glacio.running.runtime.io.MultiLoader;
import com.github.fridujo.glacio.running.runtime.io.ResourceLoader;

public class GlacioTestEngine extends HierarchicalTestEngine<GlacioEngineExecutionContext> {

    public static final String ENGINE_ID = "glacio";
    private static final String GROUP_ID = "com.github.fridujo";
    private static final String ARTIFACT_ID = "glacio-junit-engine";

    private static final String DISABLED_PROPERTY_NAME = ARTIFACT_ID + ".disabled";
    private static final String GLUE_PATHS_PARAMETER = ARTIFACT_ID + ".gluePaths";
    private static final String FEATURE_PATHS_PARAMETER = ARTIFACT_ID + ".featurePaths";

    @Override
    public String getId() {
        return ENGINE_ID;
    }

    public Optional<String> getGroupId() {
        return Optional.of(GROUP_ID);
    }

    public Optional<String> getArtifactId() {
        return Optional.of(ARTIFACT_ID);
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        boolean disableEngine = discoveryRequest
            .getConfigurationParameters()
            .get(DISABLED_PROPERTY_NAME)
            .filter(s -> "true".equals(s))
            .isPresent();

        final Set<Feature> features;
        if (disableEngine) {
            features = Collections.emptySet();
        } else {
            features = loadFeatures(discoveryRequest);
        }

        return new GlacioEngineDescriptor(uniqueId, features);
    }

    private Set<Feature> loadFeatures(EngineDiscoveryRequest discoveryRequest) {
        FeatureLoader featureLoader = getFeatureLoader();

        Set<String> featurePaths = extractPathsFromRequest(
            discoveryRequest.getConfigurationParameters(),
            FEATURE_PATHS_PARAMETER,
            "classpath:features"
        );

        return featureLoader.load(featurePaths);
    }

    private Set<String> extractPathsFromRequest(ConfigurationParameters configurationParameters,
                                                String parameterName,
                                                String defaultValue) {
        Set<String> featurePaths = new HashSet<>(
            Arrays.asList(
                configurationParameters
                    .get(parameterName)
                    .orElse(defaultValue)
                    .split(",")
            )
        );
        featurePaths.removeIf(String::isEmpty);
        return featurePaths;
    }

    private FeatureLoader getFeatureLoader() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        return new FileFeatureLoader(resourceLoader);
    }

    @Override
    protected GlacioEngineExecutionContext createExecutionContext(ExecutionRequest executionRequest) {
        Set<String> gluePaths = extractPathsFromRequest(
            executionRequest.getConfigurationParameters(),
            GLUE_PATHS_PARAMETER,
            ""
        );

        GlueFactory glueFactory = new GlueFactory();
        ExecutableLookup executableLookup = new JavaExecutableLookup(
            Runtime.class.getClassLoader(),
            gluePaths,
            glueFactory
        );
        return new GlacioEngineExecutionContext(executableLookup, glueFactory);
    }
}
