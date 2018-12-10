package com.github.fridujo.glacio.running.runtime.glue;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.fridujo.glacio.running.api.extension.ExtensionContext;

class GlueFactoryTest {

    private final GlueFactory glueFactory = new GlueFactory(emptySet(), mock(ExtensionContext.class));

    static Stream<Arguments> valid_class_supplier() {
        return Stream.of(
            Arguments.of(String.class),
            Arguments.of(TestGlue.class)
        );
    }

    static Stream<Arguments> invalid_class_supplier() {
        return Stream.of(
            Arguments.of("parameters", TestGlueWithParameter.class),
            Arguments.of("private constructor", PrivateTestGlue.class)
        );
    }

    @ParameterizedTest
    @MethodSource("valid_class_supplier")
    void can_build_any_class_having_a_constructor_without_parameters(Class<?> clazz) {
        assertThat(glueFactory.getGlue(clazz)).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("invalid_class_supplier")
    void can_not_build_a_class_with_parameters(String category, Class<?> clazz) {
        assertThatExceptionOfType(IllegalStateException.class)
            .as("class with " + category)
            .isThrownBy(() -> glueFactory.getGlue(clazz))
            .withMessage("Cannot instantiate stepdef class " + clazz.getName());
    }

    @Test
    void before_reset_object_cache() {
        List<Object> list = (List<Object>) glueFactory.getGlue(ArrayList.class);
        list.add(new Object());
        assertThat((List<Object>) glueFactory.getGlue(ArrayList.class)).hasSize(1);

        glueFactory.beforeExample();

        assertThat((List<Object>) glueFactory.getGlue(ArrayList.class)).hasSize(0);
    }

    static final class TestGlue {
    }

    private static final class PrivateTestGlue {
    }

    static final class TestGlueWithParameter {

        private final String param;

        TestGlueWithParameter(String param) {
            this.param = param;
        }

        public String getParam() {
            return param;
        }
    }
}
