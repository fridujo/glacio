package com.github.fridujo.glacio.running.runtime.glue;

import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.GlacioRunnerException;

public class MissingStepImplementationException extends GlacioRunnerException {

    public MissingStepImplementationException(Step noMatchingCodeStep) {
        super("No matching step def found for step: " + noMatchingCodeStep.getLine());
    }
}
