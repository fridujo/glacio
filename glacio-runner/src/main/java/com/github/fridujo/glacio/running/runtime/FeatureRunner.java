package com.github.fridujo.glacio.running.runtime;


import com.github.fridujo.glacio.model.Example;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.event.EventAware;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;
import com.github.fridujo.glacio.running.runtime.reporting.Reporter;

import java.util.List;

class FeatureRunner {

    private final ExecutableLookup executableLookup;
    private final EventAware eventAware;
    private final Reporter reporter;

    public FeatureRunner(ExecutableLookup executableLookup, EventAware eventAware, Reporter reporter) {
        this.executableLookup = executableLookup;
        this.eventAware = eventAware;
        this.reporter = reporter;
    }

    public void runFeature(Feature feature) {
        eventAware.beforeFeature();
        reporter.feature(feature);
        List<Example> examples = feature.getExamples();
        for (Example example : examples) {
            runExample(example);
        }
        eventAware.afterFeature();
    }

    private void runExample(Example example) {
        eventAware.beforeExample();
        reporter.example(example);
        runSteps(1, example.getSteps());
        eventAware.afterExample();
    }

    private void runSteps(int level, List<Step> steps) {
        for (Step step : steps) {
            reporter.step(level, step);
            runStep(level, step);
        }
    }

    private void runStep(int level, Step step) {
        if (step.getSubsteps().isEmpty()) {
            ExecutionResult executionResult = executableLookup.lookup(step).execute();
            reporter.result(executionResult);
        } else {
            runSteps(level + 1, step.getSubsteps());
        }
    }
}
