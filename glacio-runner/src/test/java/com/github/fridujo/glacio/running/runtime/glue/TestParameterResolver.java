package com.github.fridujo.glacio.running.runtime.glue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.api.extension.ParameterContext;
import com.github.fridujo.glacio.running.api.extension.ParameterResolver;

public class TestParameterResolver implements ParameterResolver {
    private final BiFunction<ParameterContext, ExtensionContext, Boolean> predicate;
    private final BiFunction<ParameterContext, ExtensionContext, Object> creator;

    private TestParameterResolver(BiFunction<ParameterContext, ExtensionContext, Boolean> predicate, BiFunction<ParameterContext, ExtensionContext, Object> creator) {
        this.predicate = predicate;
        this.creator = creator;
    }

    public static ParameterResolver noMatch() {
        return new TestParameterResolver((p, e) -> false, (p, e) -> null);
    }

    public static ParameterResolver forType(Object value) {
        return new TestParameterResolver(
            (p, e) ->
                (value == null ||
                    p.getParameter().getType().isAssignableFrom(value.getClass())),
            (p, e) -> value);
    }

    public static ParameterResolver atPosition(int index, Object value) {
        return new TestParameterResolver(
            (p, e) -> p.getIndex() == index,
            (p, e) -> value);
    }


    public static Set<ParameterResolver> set(ParameterResolver... parameterResolvers) {
        return Arrays.stream(parameterResolvers).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return predicate.apply(parameterContext, extensionContext);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return creator.apply(parameterContext, extensionContext);
    }
}
