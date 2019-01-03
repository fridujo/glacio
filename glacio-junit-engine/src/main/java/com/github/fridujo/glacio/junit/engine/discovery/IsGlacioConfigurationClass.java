package com.github.fridujo.glacio.junit.engine.discovery;

import java.util.function.Predicate;

import com.github.fridujo.glacio.running.api.configuration.GlacioConfiguration;

public class IsGlacioConfigurationClass implements Predicate<Class<?>> {
    @Override
    public boolean test(Class<?> candidate) {
        return candidate.isAnnotationPresent(GlacioConfiguration.class);
    }
}
