package com.github.fridujo.glacio.running.runtime;

import java.util.List;

import com.github.fridujo.glacio.model.Example;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.runtime.configuration.ConfigurationContext;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;

public class FeatureRunner {
    private final ExecutableLookup executableLookup;
    private final ConfigurationContext configurationContext;
    private final ExtensionContext extensionContext;

    public FeatureRunner(ExecutableLookup executableLookup,
                         ConfigurationContext configurationContext,
                         ExtensionContext extensionContext) {
        this.executableLookup = executableLookup;
        this.configurationContext = configurationContext;
        this.extensionContext = extensionContext;
    }

    public void runFeature(Feature feature) {
        List<Example> examples = feature.getExamples();
        for (Example example : examples) {
            runExample(example);
        }
    }

    private void runExample(Example example) {
        configurationContext.beforeExample(extensionContext);
        runSteps(1, example.getSteps());
    }

    private void runSteps(int level, List<Step> steps) {
        for (Step step : steps) {
            runStep(level, step);
        }
    }

    private void runStep(int level, Step step) {
        if (step.getSubsteps().isEmpty()) {
            ExecutionResult executionResult = executableLookup.lookup(step).execute();
        } else {
            runSteps(level + 1, step.getSubsteps());
        }
    }
}
