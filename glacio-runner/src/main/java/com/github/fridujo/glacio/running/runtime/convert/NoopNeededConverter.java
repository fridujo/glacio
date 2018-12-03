package com.github.fridujo.glacio.running.runtime.convert;

import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.Value;

public class NoopNeededConverter extends AbstractPositionedParameterConverter {
    @Override
    public Value convert(Value rawValue, ParameterDescriptor parameterDescriptor) {
        return rawValue.filter(v -> parameterDescriptor.type.isAssignableFrom(v.clazz));
    }
}
