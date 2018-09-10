package com.github.fridujo.glacio.running.runtime;

import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.GlacioRunnerException;

public class MissingStepImplementationException extends GlacioRunnerException {

    public MissingStepImplementationException(Step noMatchingCodeStep) {
        super("Not matching step def found for step: " + noMatchingCodeStep.getLine());
    }
}
