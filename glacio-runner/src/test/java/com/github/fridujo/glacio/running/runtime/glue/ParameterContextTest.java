package com.github.fridujo.glacio.running.runtime.glue;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.extension.ParameterContext;
import com.github.fridujo.glacio.running.runtime.extension.ParameterContextImpl;

class ParameterContextTest {

    @Test
    void toString_with_constructor_strips_package() throws NoSuchMethodException {
        Constructor<String> constructor = String.class.getDeclaredConstructor(String.class);
        ParameterContext parameterContext = new ParameterContextImpl(constructor, constructor.getParameters()[0], 0);

        assertThat(parameterContext).hasToString("String([0] String arg0)");
    }

    @Test
    void toString_with_method() throws NoSuchMethodException {
        Method method = String.class.getDeclaredMethod("charAt", int.class);
        ParameterContext parameterContext = new ParameterContextImpl(method, method.getParameters()[0], 0);

        assertThat(parameterContext).hasToString("charAt([0] int arg0)");
    }
}
