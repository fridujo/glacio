package com.github.fridujo.glacio.running.runtime.glue;

import static com.github.fridujo.glacio.running.runtime.glue.TestParameterResolver.atPosition;
import static com.github.fridujo.glacio.running.runtime.glue.TestParameterResolver.forType;
import static com.github.fridujo.glacio.running.runtime.glue.TestParameterResolver.noMatch;
import static com.github.fridujo.glacio.running.runtime.glue.TestParameterResolver.set;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.fridujo.glacio.running.GlacioRunnerException;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;

class GlueFactoryTest {

    private final GlueFactory glueFactory = new GlueFactory(singleton(noMatch()),
        mock(ExtensionContext.class));

    static Stream<Arguments> valid_class_supplier() {
        return Stream.of(
            Arguments.of(String.class),
            Arguments.of(TestGlue.class)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void before_reset_object_cache() {
        List<Object> list = (List<Object>) glueFactory.getGlue(ArrayList.class);
        list.add(new Object());
        assertThat((List<Object>) glueFactory.getGlue(ArrayList.class)).hasSize(1);

        glueFactory.reset();

        assertThat((List<Object>) glueFactory.getGlue(ArrayList.class)).hasSize(0);
    }

    @Test
    void cannot_build_a_class_with_private_constructor() {
        assertThatExceptionOfType(GlacioRunnerException.class)
            .as("class with private constructor")
            .isThrownBy(() -> glueFactory.getGlue(PrivateTestGlue.class))
            .withMessage("Cannot create instance of class " + PrivateTestGlue.class.getSimpleName() + ": constructor is private");
    }

    @Test
    void cannot_build_a_class_with_no_parameter_resolver_matching() {
        assertThatExceptionOfType(GlacioRunnerException.class)
            .as("class with private constructor")
            .isThrownBy(() -> glueFactory.getGlue(TestGlueWithParameter.class))
            .withMessageStartingWith("Cannot create instance of class " + TestGlueWithParameter.class.getSimpleName() +
                ": No ParameterResolver matches parameter GlueFactoryTest$TestGlueWithParameter([0] String");
    }

    @Test
    void cannot_build_a_class_with_multiple_constructors() {
        assertThatExceptionOfType(GlacioRunnerException.class)
            .as("class with private constructor")
            .isThrownBy(() -> glueFactory.getGlue(MultiConstructorGlue.class))
            .withMessage("Cannot create instance of class " + MultiConstructorGlue.class.getSimpleName() +
                ": must have only one constructor, or a default one");
    }

    @Test
    void cannot_build_class_with_throwing_constructor() {
        assertThatExceptionOfType(GlacioRunnerException.class)
            .as("class with throwing constructor")
            .isThrownBy(() -> glueFactory.getGlue(ThrowingConstructor.class))
            .withMessageContaining("Cannot create instance of class " + ThrowingConstructor.class.getSimpleName())
            .withCauseExactlyInstanceOf(InvocationTargetException.class);
    }

    @ParameterizedTest
    @MethodSource("valid_class_supplier")
    void can_build_any_class_having_a_constructor_without_parameters(Class<?> clazz) {
        assertThat(glueFactory.getGlue(clazz)).isNotNull();
    }

    @Test
    void can_build_a_class_with_matching_parameter_resolver_by_type() {
        GlueFactory glueFactory = new GlueFactory(set(noMatch(), forType("test")), mock(ExtensionContext.class));
        TestGlueWithParameter glue = glueFactory.getGlue(TestGlueWithParameter.class);

        assertThat(glue.param).isEqualTo("test");
    }

    @Test
    void can_build_a_class_with_matching_parameter_resolver_by_position() {
        GlueFactory glueFactory = new GlueFactory(set(noMatch(), atPosition(0, "test2")), mock(ExtensionContext.class));
        TestGlueWithParameter glue = glueFactory.getGlue(TestGlueWithParameter.class);

        assertThat(glue.param).isEqualTo("test2");
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
    }

    static final class MultiConstructorGlue {
        MultiConstructorGlue(String param) {
        }

        MultiConstructorGlue(int param) {
        }
    }

    static final class ThrowingConstructor {
        ThrowingConstructor() {
            throw new RuntimeException("test ex");
        }
    }
}
