package com.github.fridujo.glacio.running.runtime.glue;

import com.github.fridujo.glacio.model.Step;

public interface ExecutableLookup {

    Executable lookup(Step step) throws MissingStepImplementationException, AmbiguousStepDefinitionsException;
}
