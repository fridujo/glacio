package com.github.fridujo.glacio.running.runtime.convert;

import com.github.fridujo.glacio.running.GlacioRunnerException;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;

public class MissingConverterException extends GlacioRunnerException {
    public MissingConverterException(ParameterDescriptor parameterDescriptor, SourceSet sourceSet) {
        super("No converter available for " + parameterDescriptor + " given " + sourceSet);
    }
}
