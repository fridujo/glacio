package com.github.fridujo.glacio.running.runtime;

import java.util.Arrays;
import java.util.List;

import com.github.fridujo.glacio.model.Example;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;

public class FeatureRunner {
    private final ExecutableLookup executableLookup;
    private final List<BeforeExampleEventAware> eventConsumers;

    public FeatureRunner(ExecutableLookup executableLookup, BeforeExampleEventAware... eventConsumers) {
        this.executableLookup = executableLookup;
        this.eventConsumers = Arrays.asList(eventConsumers);
    }

    public void runFeature(Feature feature) {
        List<Example> examples = feature.getExamples();
        for (Example example : examples) {
            runExample(example);
        }
    }

    private void runExample(Example example) {
        eventConsumers.forEach(BeforeExampleEventAware::beforeExample);
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
