package com.github.fridujo.glacio.running.runtime.convert;

import static com.github.fridujo.glacio.running.runtime.convert.ParameterDescriptors.descriptor;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterConverterAware;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

abstract class AbstractConverterTest {

    protected final Converter converterUnderTest;
    protected final Class<?> validTargetType;
    protected final Class<?> invalidTargetType;
    protected final Object invalidSource;
    protected final Object validSource;

    protected AbstractConverterTest(Converter converterUnderTest,
                                    Object validSource,
                                    Class<?> validTargetType,
                                    Object invalidSource,
                                    Class<?> invalidTargetType) {
        this.converterUnderTest = converterUnderTest;
        this.validTargetType = validTargetType;
        this.invalidTargetType = invalidTargetType;
        this.invalidSource = invalidSource;
        this.validSource = validSource;
        if (converterUnderTest instanceof ParameterConverterAware) {
            ((ParameterConverterAware) converterUnderTest).setConverter(new MethodParameterConverter());
        }
    }

    protected ParameterDescriptor validDescriptor() {
        return descriptor(validTargetType);
    }

    @Test
    void missing_source_is_not_converted() {
        SourceSet sourceSet = SourceSet.empty();
        ParameterDescriptor parameterDescriptor = descriptor(validTargetType);

        Value converted = converterUnderTest.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isFalse();
    }

    @Test
    void invalid_source_type_is_not_converted() {
        SourceSet sourceSet = SourceSet.fromRaw(invalidSource);
        ParameterDescriptor parameterDescriptor = descriptor(String.class);

        Value converted = converterUnderTest.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isFalse();
    }

    @Test
    void not_matching_target_type_is_not_converted() {
        SourceSet sourceSet = SourceSet.fromRaw(validSource);
        ParameterDescriptor parameterDescriptor = descriptor(invalidTargetType);

        Value converted = converterUnderTest.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isFalse();
    }
}
