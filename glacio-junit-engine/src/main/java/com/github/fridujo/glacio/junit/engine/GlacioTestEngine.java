package com.github.fridujo.glacio.junit.engine;

import com.github.fridujo.glacio.junit.engine.descriptor.GlacioEngineDescriptor;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.running.runtime.GlacioRunnerInitializationException;
import com.github.fridujo.glacio.running.runtime.Runtime;
import com.github.fridujo.glacio.running.runtime.event.EventAware;
import com.github.fridujo.glacio.running.runtime.event.NoopEventAware;
import com.github.fridujo.glacio.running.runtime.feature.FeatureLoader;
import com.github.fridujo.glacio.running.runtime.feature.FileFeatureLoader;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;
import com.github.fridujo.glacio.running.runtime.glue.GlueFactory;
import com.github.fridujo.glacio.running.runtime.glue.JavaExecutableLookup;
import com.github.fridujo.glacio.running.runtime.glue.SimpleGlueFactory;
import com.github.fridujo.glacio.running.runtime.io.MultiLoader;
import com.github.fridujo.glacio.running.runtime.io.ResourceLoader;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GlacioTestEngine extends HierarchicalTestEngine<GlacioEngineExecutionContext> {

    public static final String ENGINE_ID = "glacio";
    private static final String GLUE_PATHS_PARAMETER = "glacio.gluePaths";

    @Override
    public String getId() {
        return ENGINE_ID;
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        FeatureLoader featureLoader = new FileFeatureLoader(resourceLoader);

        Set<String> gluePaths = new HashSet<>(
            Arrays.asList(
                discoveryRequest
                    .getConfigurationParameters()
                    .get("glacio.featurePaths")
                    .orElse("classpath:features")
                    .split(",")
            )
        );
        Set<Feature> features = featureLoader.load(gluePaths);

        return new GlacioEngineDescriptor(uniqueId, features);
    }

    @Override
    protected GlacioEngineExecutionContext createExecutionContext(ExecutionRequest executionRequest) {
        Set<String> gluePaths = new HashSet<>(
            Arrays.asList(
                executionRequest
                    .getConfigurationParameters()
                    .get(GLUE_PATHS_PARAMETER)
                    .orElseThrow(() -> new GlacioRunnerInitializationException(
                        "No glue paths is configured, please supply them using parameter '" +
                            GLUE_PATHS_PARAMETER +
                            "', comma separated"
                    ))
                    .split(",")
            )
        );

        GlueFactory glueFactory = new SimpleGlueFactory();
        EventAware eventAware;
        if (glueFactory instanceof EventAware) {
            eventAware = (EventAware) glueFactory;
        } else {
            eventAware = new NoopEventAware();
        }
        ExecutableLookup executableLookup = new JavaExecutableLookup(
            Runtime.class.getClassLoader(),
            gluePaths,
            glueFactory
        );
        return new GlacioEngineExecutionContext(executableLookup, eventAware);
    }
}
