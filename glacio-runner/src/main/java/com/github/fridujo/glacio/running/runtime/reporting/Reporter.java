package com.github.fridujo.glacio.running.runtime.reporting;

import com.github.fridujo.glacio.model.Example;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.ExecutionResult;

// TODO not happy with this interface
// - the "append" strategy will not work with parallel runs
//     - a pointer to the right testItem should be added to each reporting method
public interface Reporter {

    String getName();

    void feature(Feature feature);

    void example(Example example);

    void step(int level, Step step);

    void result(ExecutionResult executionResult);

    void done();
}
