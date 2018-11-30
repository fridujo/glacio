package com.github.fridujo.glacio.running.runtime.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

class MethodParameterConverterTest {

    @Test
    void conversion_throws_when_no_provider_matches() {
        MethodParameterConverter methodParameterConverter = new MethodParameterConverter(Collections.singleton(new NotMatchingConverter()));

        assertThatExceptionOfType(MissingConverterException.class)
            .isThrownBy(() -> methodParameterConverter.convert(
                new Object[]{"test"},
                StringBuilder.class.getDeclaredMethod("append", String.class)
            ))
            .withMessage("No converter available for " +
                "Parameter(position=0, type=class java.lang.String)" +
                " of " +
                "public java.lang.StringBuilder java.lang.StringBuilder.append(java.lang.String)" +
                " given SourceSet " +
                "[test]");
    }

    @Test
    void default_converter_nominal_uses() throws NoSuchMethodException {
        MethodParameterConverter methodParameterConverter = new MethodParameterConverter();
        Object[] rawParameters = new Object[]{"test", 1, new NotMatchingConverter()};
        Method sampleMethod = TestInterface.class.getDeclaredMethod("sampleMethod", String.class, int.class, Object.class);
        Object[] parameters = methodParameterConverter.convert(rawParameters, sampleMethod);

        assertThat(parameters).isEqualTo(rawParameters);
    }

    interface TestInterface {
        void sampleMethod(String p1, int p2, Object p3);
    }

    static class NotMatchingConverter implements Converter {

        @Override
        public Value convert(SourceSet sourceSet, ParameterDescriptor parameterDescriptor) {
            return Value.absent();
        }
    }
}
