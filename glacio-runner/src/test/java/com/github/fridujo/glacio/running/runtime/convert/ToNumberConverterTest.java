package com.github.fridujo.glacio.running.runtime.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

class ToNumberConverterTest {

    private final Converter toNumberConverter = new ToNumberConverter();

    static List<Arguments> conversion_test_cases() {
        return Arrays.asList(
            arguments("0x71", byte.class, (byte) 113),
            arguments("41", Byte.class, (byte) 41),

            arguments("-0XA2", short.class, (short) -162),
            arguments("42", Short.class, (short) 42),

            arguments("#A3", int.class, 163),
            arguments("43", Integer.class, 43),

            arguments("0xA4", long.class, 164L),
            arguments("44_000", Long.class, 44_000L),

            arguments("0xA5", BigInteger.class, new BigInteger("165")),
            arguments("-0XA6", BigInteger.class, new BigInteger("166").negate()),
            arguments("-#034", BigInteger.class, new BigInteger("-52")),
            arguments("-0", BigInteger.class, BigInteger.ZERO),
            arguments("0757", BigInteger.class, new BigInteger("495")),
            arguments("34 000", BigInteger.class, new BigInteger("34000")),

            arguments("46.56", float.class, 46.56F),

            arguments("47.56", double.class, 47.56D),

            arguments("2 000", BigDecimal.class, new BigDecimal("2000"))
        );
    }

    @ParameterizedTest
    @MethodSource("conversion_test_cases")
    void string_to_number_conversion(String input, Class<?> targetType, Object expectedValue) {
        SourceSet sourceSet = SourceSet.fromRaw(input);
        ParameterDescriptor parameterDescriptor = new ParameterDescriptor(0, targetType, null);
        Value converted = toNumberConverter.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isTrue();
        assertThat(converted.value).isEqualTo(expectedValue);
    }

    @Test
    void no_conversion_when_source_is_absent() {
        SourceSet sourceSet = SourceSet.empty();
        ParameterDescriptor parameterDescriptor = new ParameterDescriptor(0, int.class, null);
        Value converted = toNumberConverter.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isFalse();
    }

    @Test
    void no_conversion_when_target_type_is_not_a_number() {
        SourceSet sourceSet = SourceSet.fromRaw("2");
        ParameterDescriptor parameterDescriptor = new ParameterDescriptor(0, String.class, null);
        Value converted = toNumberConverter.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isFalse();
    }
}
