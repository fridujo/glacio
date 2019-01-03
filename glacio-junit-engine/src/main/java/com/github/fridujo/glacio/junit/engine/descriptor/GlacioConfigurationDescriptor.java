package com.github.fridujo.glacio.junit.engine.descriptor;

import java.util.Set;

import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.running.runtime.configuration.ConfigurationContext;
import com.github.fridujo.glacio.running.runtime.extension.ExtensionContextImpl;
import com.github.fridujo.glacio.running.runtime.feature.FeatureLoader;
import com.github.fridujo.glacio.running.runtime.feature.FileFeatureLoader;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;
import com.github.fridujo.glacio.running.runtime.glue.JavaExecutableLookup;
import com.github.fridujo.glacio.running.runtime.io.MultiLoader;

public class GlacioConfigurationDescriptor extends AbstractGlacioTestDescriptor {

    public static final String SEGMENT_TYPE = "configuration";
    private final ConfigurationContext configurationContext;
    private final ExtensionContextImpl extensionContext;

    public GlacioConfigurationDescriptor(UniqueId parentUniqueId, ConfigurationContext configurationContext) {
        super(parentUniqueId.append(SEGMENT_TYPE, configurationContext.name()),
            parentUniqueId.append(SEGMENT_TYPE, configurationContext.name()),
            configurationContext.name());
        this.configurationContext = configurationContext;
        extensionContext = new ExtensionContextImpl(configurationContext.getConfigurationClass());

        loadFeatures(configurationContext).forEach(feature -> addChild(new FeatureDescriptor(configurationId, getUniqueId(), feature)));
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    private Set<Feature> loadFeatures(ConfigurationContext configurationContext) {
        ClassLoader contextClassLoader = configurationContext.getClassLoader();
        FeatureLoader featureLoader = new FileFeatureLoader(new MultiLoader(contextClassLoader));
        return featureLoader.load(configurationContext.getFeaturePaths());
    }

    @Override
    public GlacioEngineExecutionContext prepare(GlacioEngineExecutionContext context) {
        ExecutableLookup executableLookup = new JavaExecutableLookup(
            configurationContext.getClassLoader(),
            configurationContext.getGluePaths());

        return context.initializeConfiguration(getUniqueId(), executableLookup, configurationContext, extensionContext);
    }

    @Override
    public GlacioEngineExecutionContext before(GlacioEngineExecutionContext context) {
        configurationContext.beforeConfiguration(extensionContext);
        return context;
    }
}
