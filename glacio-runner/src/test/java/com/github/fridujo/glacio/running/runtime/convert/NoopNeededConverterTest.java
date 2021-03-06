package com.github.fridujo.glacio.running.runtime.convert;

import static com.github.fridujo.glacio.running.runtime.convert.ParameterDescriptors.descriptor;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

class NoopNeededConverterTest {

    private final Converter converter = new NoopNeededConverter();

    @Test
    void source_matches_when_target_is_a_supertype() {
        SourceSet sourceSet = SourceSet.fromRaw("test");
        ParameterDescriptor parameterDescriptor = descriptor(Object.class);
        Value value = converter.convert(sourceSet, parameterDescriptor);

        assertThat(value.present).isTrue();
        assertThat(value.value).isEqualTo("test");
    }

    @Test
    void source_mismatches_when_target_is_a_subtype() {
        SourceSet sourceSet = SourceSet.fromRaw(new Object());
        ParameterDescriptor parameterDescriptor = descriptor(String.class);
        Value value = converter.convert(sourceSet, parameterDescriptor);

        assertThat(value.present).isFalse();
    }

    @Test
    void returns_absent_when_source_has_no_matching_position() {
        SourceSet sourceSet = SourceSet.fromRaw("test");
        ParameterDescriptor parameterDescriptor = descriptor(1, Object.class);
        Value value = converter.convert(sourceSet, parameterDescriptor);

        assertThat(value.present).isFalse();
    }
}
