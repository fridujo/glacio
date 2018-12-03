package com.github.fridujo.glacio.running.runtime.convert;

import static com.github.fridujo.glacio.running.runtime.convert.ParameterDescriptors.descriptor;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterConverterAware;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;
import com.github.fridujo.glacio.running.runtime.convert.ParameterDescriptors.TypeReference;

class ToOptionalConverterTest {

    private final Converter converterUnderTest = new ToOptionalConverter();

    @Test
    void missing_source_converts_to_empty() {
        SourceSet sourceSet = SourceSet.empty();
        ParameterDescriptor parameterDescriptor = descriptor(new TypeReference<Optional<Integer>>() {
        });

        Value converted = converterUnderTest.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isTrue();
        assertThat((Optional<Integer>) converted.value).isEmpty();
    }

    @Test
    void not_matching_target_type_is_not_converted() {
        SourceSet sourceSet = SourceSet.empty();
        ParameterDescriptor parameterDescriptor = descriptor(String.class);

        Value converted = converterUnderTest.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isFalse();
    }

    @Test
    void nominal_conversion() {
        SourceSet sourceSet = SourceSet.fromRaw("test");
        ParameterDescriptor parameterDescriptor = descriptor(new TypeReference<Optional<String>>() {
        });

        Value converted = converterUnderTest.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isTrue();
        assertThat((Optional<String>) converted.value).hasValue("test");
    }

    @Test
    void null_conversion() {
        SourceSet sourceSet = SourceSet.fromRaw(new Object[]{null});
        ParameterDescriptor parameterDescriptor = descriptor(new TypeReference<Optional<String>>() {
        });

        Value converted = converterUnderTest.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isTrue();
        assertThat((Optional<String>) converted.value).isEmpty();
    }

    @Test
    void conversion_without_generics() {
        Exception obj = new IllegalStateException("test");
        SourceSet sourceSet = SourceSet.fromRaw(obj);
        ParameterDescriptor parameterDescriptor = descriptor(Optional.class);

        Value converted = converterUnderTest.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isTrue();
        assertThat((Optional<Object>) converted.value).hasValue(obj);
    }

    @Test
    void nominal_conversion_with_parameter_conversion() {
        SourceSet sourceSet = SourceSet.fromRaw("45_000");
        ParameterDescriptor parameterDescriptor = descriptor(new TypeReference<Optional<Integer>>() {
        });

        ((ParameterConverterAware) converterUnderTest).setConverter(new MethodParameterConverter());

        Value converted = converterUnderTest.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isTrue();
        assertThat((Optional<Integer>) converted.value).hasValue(45_000);
    }
}
