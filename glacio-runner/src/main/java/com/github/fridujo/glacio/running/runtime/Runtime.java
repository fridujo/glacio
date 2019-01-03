package com.github.fridujo.glacio.running.runtime;

import java.util.Set;

import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.running.runtime.configuration.ConfigurationContext;
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
            JavaExecutableLookup executableLookup = new JavaExecutableLookup(
                classLoader,
                configurationContext.getGluePaths());

            Set<Feature> features = featureLoader.load(configurationContext.getFeaturePaths());
            FeatureRunner featureRunner = new FeatureRunner(executableLookup, configurationContext);
            features.forEach(featureRunner::runFeature);
        }
    }
}
