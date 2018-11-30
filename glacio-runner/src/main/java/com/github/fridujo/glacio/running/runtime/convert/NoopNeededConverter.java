package com.github.fridujo.glacio.running.runtime.convert;

import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

public class NoopNeededConverter implements Converter {
    @Override
    public Value convert(SourceSet sourceSet, ParameterDescriptor parameterDescriptor) {
        Value value = sourceSet.atPosition(parameterDescriptor.position);
        if (value.present && parameterDescriptor.type.isAssignableFrom(value.clazz)) {
            return value;
        }
        return Value.absent();
    }
}
