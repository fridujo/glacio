package com.github.fridujo.glacio.running.runtime.convert;

import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

public abstract class AbstractPositionedParameterConverter implements Converter {

    @Override
    public final Value convert(SourceSet sourceSet, ParameterDescriptor parameterDescriptor) {
        Value rawValue = sourceSet.atPosition(parameterDescriptor.position);
        return convert(rawValue, parameterDescriptor);
    }

    protected abstract Value convert(Value rawValue, ParameterDescriptor parameterDescriptor);
}
