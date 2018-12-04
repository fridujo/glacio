package com.github.fridujo.glacio.running.runtime.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterConverter;
import com.github.fridujo.glacio.running.api.convert.ParameterConverterAware;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

public class MethodParameterConverter implements ParameterConverter {

    private final Set<Converter> converters;

    MethodParameterConverter(Set<Converter> converters) {
        this.converters = converters;
        this.converters.forEach(c -> {
            if (c instanceof ParameterConverterAware) {
                ((ParameterConverterAware) c).setConverter(this);
            }
        });
    }

    public MethodParameterConverter() {
        this(loadConvertersFromClasspath());
    }

    private static Set<Converter> loadConvertersFromClasspath() {
        Set<Converter> converters = new LinkedHashSet<>();
        for (Converter converter : ServiceLoader.load(Converter.class)) {
            converters.add(converter);
        }
        return converters;
    }

    public Object[] convert(Object[] rawParameters, Executable executable) {
        SourceSet sourceSet = SourceSet.fromRaw(rawParameters);
        List<ParameterDescriptor> parameterDescriptors = buildParameterDescriptors(executable);

        List<Object> parameters = convert(sourceSet, parameterDescriptors);

        return parameters.toArray();
    }

    private List<Object> convert(SourceSet sourceSet, List<ParameterDescriptor> parameterDescriptors) {
        return parameterDescriptors
            .stream()
            .map(parameterDescriptor -> convert(sourceSet, parameterDescriptor))
            .collect(Collectors.toList());
    }

    public Object convert(SourceSet sourceSet, ParameterDescriptor parameterDescriptor) {
        for (Converter converter : converters) {
            Value value = converter.convert(sourceSet, parameterDescriptor);
            if (value.present) {
                return value.value;
            }
        }
        throw new MissingConverterException(parameterDescriptor, sourceSet);
    }

    private List<ParameterDescriptor> buildParameterDescriptors(Executable method) {
        List<ParameterDescriptor> parameterDescriptors = new ArrayList<>();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        for (int i = 0; i < method.getParameterCount(); i++) {
            List<Annotation> annotations = Arrays.asList(method.getParameterAnnotations()[i]);
            parameterDescriptors.add(new ParameterDescriptor(i, parameterTypes[i], genericParameterTypes[i], annotations, method));
        }
        return parameterDescriptors;
    }
}
