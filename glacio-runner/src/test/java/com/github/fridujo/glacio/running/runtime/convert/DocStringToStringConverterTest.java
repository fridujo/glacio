package com.github.fridujo.glacio.running.runtime.convert;

import static com.github.fridujo.glacio.running.runtime.convert.ParameterDescriptors.descriptor;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.model.DocString;
import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

class DocStringToStringConverterTest {

    private final Converter docStringToStringConverter = new DocStringToStringConverter();

    @Test
    void nominal_conversion() {
        String plainTextHjson = "{some: thing}";
        SourceSet sourceSet = SourceSet.fromRaw(new DocString(Optional.of("hjson"), plainTextHjson));
        ParameterDescriptor parameterDescriptor = descriptor(String.class);

        Value converted = docStringToStringConverter.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isTrue();
        assertThat(converted.value).isEqualTo(plainTextHjson);
    }

    @Test
    void missing_source_is_not_converted() {
        SourceSet sourceSet = SourceSet.empty();
        ParameterDescriptor parameterDescriptor = descriptor(String.class);

        Value converted = docStringToStringConverter.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isFalse();
    }

    @Test
    void not_DocString_source_is_not_converted() {
        SourceSet sourceSet = SourceSet.fromRaw("test");
        ParameterDescriptor parameterDescriptor = descriptor(String.class);

        Value converted = docStringToStringConverter.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isFalse();
    }

    @Test
    void not_String_target_is_not_converted() {
        SourceSet sourceSet = SourceSet.fromRaw(new DocString(Optional.of("hjson"), "{some: thing}"));
        ParameterDescriptor parameterDescriptor = descriptor(Integer.class);

        Value converted = docStringToStringConverter.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isFalse();
    }
}
