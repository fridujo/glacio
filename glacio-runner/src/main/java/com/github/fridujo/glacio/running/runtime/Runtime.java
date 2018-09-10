package com.github.fridujo.glacio.running.runtime;

import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.running.runtime.event.EventAware;
import com.github.fridujo.glacio.running.runtime.event.NoopEventAware;
import com.github.fridujo.glacio.running.runtime.feature.FeatureLoader;
import com.github.fridujo.glacio.running.runtime.glue.GlueFactory;
import com.github.fridujo.glacio.running.runtime.glue.JavaExecutableLookup;
import com.github.fridujo.glacio.running.runtime.reporting.Reporter;

import java.util.Set;

public class Runtime {

    private final RuntimeOptions runtimeOptions;
    private final FeatureLoader featureLoader;
    private final FeatureRunner featureRunner;
    private final EventAware eventAware;

    public Runtime(RuntimeOptions runtimeOptions, GlueFactory glueFactory, FeatureLoader featureLoader, Reporter reporter) {
        this.runtimeOptions = runtimeOptions;
        if (glueFactory instanceof EventAware) {
            eventAware = (EventAware) glueFactory;
        } else {
            eventAware = new NoopEventAware();
        }
        eventAware.beforeInitialization();
        this.featureLoader = featureLoader;

        // the ExecutableLookup should be a parameter of this class
        this.featureRunner = new FeatureRunner(
            new JavaExecutableLookup(
                Runtime.class.getClassLoader(),
                runtimeOptions.getGluePaths(),
                glueFactory
            ),
            eventAware,
            reporter
        );
    }

    public void run() {
        eventAware.beforeAllFeatures();
        Set<Feature> features = featureLoader.load(runtimeOptions.getFeaturePaths());
        features.forEach(featureRunner::runFeature);

        eventAware.afterAllFeatures();
    }
}
