package com.github.fridujo.glacio.running.runtime.convert;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.model.DocString;
import com.github.fridujo.glacio.running.api.convert.Value;

class DocStringToStringConverterTest extends AbstractConverterTest {

    private static final String PLAIN_TEXT_HJSON = "{some: thing}";

    DocStringToStringConverterTest() {
        super(new DocStringToStringConverter(),
            new DocString(Optional.of("hjson"), PLAIN_TEXT_HJSON), String.class,
            "test", Integer.class);
    }

    @Test
    void nominal_conversion() {
        Value converted = converterUnderTest.convert(validSourceSet(), validDescriptor());

        assertThat(converted.present).isTrue();
        assertThat(converted.value).isEqualTo(PLAIN_TEXT_HJSON);
    }
}
