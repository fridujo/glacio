package com.github.fridujo.glacio.running.runtime.convert;

import com.github.fridujo.glacio.running.api.convert.ConvertWith;
import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

public class AnnotationBasedConverter implements Converter {

    @Override
    public Value convert(SourceSet sourceSet, ParameterDescriptor parameterDescriptor) {
        ConvertWith convertWith = parameterDescriptor.getAnnotation(ConvertWith.class);
        final Value converted;
        if (convertWith != null) {
            converted = instantiate(convertWith.value()).convert(sourceSet, parameterDescriptor);
        } else {
            converted = Value.absent();
        }
        return converted;
    }

    private Converter instantiate(Class<? extends Converter> converterClass) {
        try {
            return converterClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Specified Converter[" + converterClass.getName() + "] must have a public no-args constructor", e);
        }
    }
}
