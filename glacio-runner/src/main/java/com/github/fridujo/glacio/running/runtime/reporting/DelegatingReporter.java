package com.github.fridujo.glacio.running.runtime.reporting;

import com.github.fridujo.glacio.model.Example;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.ExecutionResult;

import java.util.Set;

public class DelegatingReporter implements Reporter {

    private final Set<Reporter> delegates;

    public DelegatingReporter(Set<Reporter> delegates) {
        this.delegates = delegates;
    }

    @Override
    public String getName() {
        throw new IllegalStateException("Must not be loaded through extension mechanism");
    }

    @Override
    public void feature(Feature feature) {
        delegates.forEach(d -> d.feature(feature));
    }


    @Override
    public void example(Example example) {
        delegates.forEach(d -> d.example(example));
    }

    @Override
    public void step(int level, Step step) {
        delegates.forEach(d -> d.step(level, step));
    }

    @Override
    public void result(ExecutionResult result) {
        delegates.forEach(d -> d.result(result));
    }

    @Override
    public void done() {
        delegates.forEach(d -> d.done());
    }
}
