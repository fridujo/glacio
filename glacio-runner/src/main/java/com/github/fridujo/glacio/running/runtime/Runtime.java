package com.github.fridujo.glacio.running.runtime;

import java.util.Set;

import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.running.runtime.feature.FeatureLoader;
import com.github.fridujo.glacio.running.runtime.glue.GlueFactory;
import com.github.fridujo.glacio.running.runtime.glue.JavaExecutableLookup;

public class Runtime {

    private final RuntimeOptions runtimeOptions;
    private final FeatureLoader featureLoader;
    private final FeatureRunner featureRunner;

    public Runtime(RuntimeOptions runtimeOptions, FeatureLoader featureLoader) {
        this.runtimeOptions = runtimeOptions;
        this.featureLoader = featureLoader;

        // the ExecutableLookup should be a parameter of this class
        GlueFactory glueFactory = new GlueFactory();
        this.featureRunner = new FeatureRunner(
            new JavaExecutableLookup(
                Runtime.class.getClassLoader(),
                runtimeOptions.getGluePaths(),
                glueFactory
            ),
            glueFactory
        );
    }

    public void run() {
        Set<Feature> features = featureLoader.load(runtimeOptions.getFeaturePaths());
        features.forEach(featureRunner::runFeature);
    }
}
