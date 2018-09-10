package com.github.fridujo.glacio.running.runtime.glue;

import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.MissingStepImplementationException;

public interface ExecutableLookup {

    Executable lookup(Step step) throws MissingStepImplementationException, AmbiguousStepDefinitionsException;
}
