package com.github.fridujo.glacio.running.runtime.convert;

import java.util.Collections;

import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterConverter;
import com.github.fridujo.glacio.running.api.convert.ParameterConverterAware;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

public abstract class AbstractPositionedParameterConverter implements Converter, ParameterConverterAware {

    private ParameterConverter parameterConverter;

    @Override
    public final Value convert(SourceSet sourceSet, ParameterDescriptor parameterDescriptor) {
        Value rawValue = sourceSet.atPosition(parameterDescriptor.position);
        return convert(rawValue, parameterDescriptor);
    }

    protected abstract Value convert(Value rawValue, ParameterDescriptor parameterDescriptor);

    @SuppressWarnings("unchecked")
    protected <T> T convertTo(Object value, Class<T> keyType) {
        if (value == null || keyType == null || value.getClass() == keyType) {
            return (T) value;
        }
        if (parameterConverter == null) {
            throw new IllegalStateException("ParameterConverter must be set to perform parameter conversion");
        }
        return (T) parameterConverter.convert(SourceSet.fromRaw(value), new ParameterDescriptor(0, keyType, keyType, Collections.emptyList(), null));
    }

    @Override
    public void setConverter(ParameterConverter parameterConverter) {
        this.parameterConverter = parameterConverter;
    }
}
