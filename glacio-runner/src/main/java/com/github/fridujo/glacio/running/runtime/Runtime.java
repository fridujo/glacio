package com.github.fridujo.glacio.running.runtime;

import java.util.Set;

import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.runtime.configuration.ConfigurationContext;
import com.github.fridujo.glacio.running.runtime.extension.ExtensionContextImpl;
import com.github.fridujo.glacio.running.runtime.feature.FeatureLoader;
import com.github.fridujo.glacio.running.runtime.feature.FileFeatureLoader;
import com.github.fridujo.glacio.running.runtime.glue.JavaExecutableLookup;
import com.github.fridujo.glacio.running.runtime.io.MultiLoader;

public class Runtime {

    private final Set<ConfigurationContext> configurationContexts;

    public Runtime(Set<ConfigurationContext> configurationContexts) {
        this.configurationContexts = configurationContexts;
    }

    public void run() {
        for (ConfigurationContext configurationContext : configurationContexts) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            FeatureLoader featureLoader = new FileFeatureLoader(new MultiLoader(classLoader));
            ExtensionContext extensionContext = new ExtensionContextImpl(configurationContext.getConfigurationClass());
            JavaExecutableLookup executableLookup = new JavaExecutableLookup(
                classLoader,
                configurationContext.getGluePaths());
            configurationContext.enrichWith(executableLookup);

            Set<Feature> features = featureLoader.load(configurationContext.getFeaturePaths());
            configurationContext.beforeConfiguration(extensionContext);
            try {
                FeatureRunner featureRunner = new FeatureRunner(executableLookup, configurationContext, extensionContext);
                features.forEach(featureRunner::runFeature);
            } finally {
                configurationContext.afterConfiguration(extensionContext);
            }
        }
    }
}
