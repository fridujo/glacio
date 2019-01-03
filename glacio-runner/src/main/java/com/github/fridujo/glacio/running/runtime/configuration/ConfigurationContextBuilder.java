package com.github.fridujo.glacio.running.runtime.configuration;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.fridujo.glacio.running.api.configuration.GlacioConfiguration;
import com.github.fridujo.glacio.running.runtime.GlacioRunnerInitializationException;
import com.github.fridujo.glacio.running.runtime.RuntimeOptions;

public class ConfigurationContextBuilder {

    public Set<ConfigurationContext> fromRuntimeOptions(RuntimeOptions runtimeOptions) {
        if (runtimeOptions.getConfigurationClasses().isEmpty()) {
            return Collections.singleton(defaultConfiguration(runtimeOptions));
        } else {
            return runtimeOptions.getConfigurationClasses()
                .stream()
                .map(this::fromClass)
                .collect(toCollection(LinkedHashSet::new));
        }
    }

    public ConfigurationContext defaultConfiguration(RuntimeOptions runtimeOptions) {
        return new ConfigurationContext(
            null,
            runtimeOptions.getGluePaths(),
            runtimeOptions.getFeaturePaths()
        );
    }

    public ConfigurationContext fromClass(Class<?> configurationClass) {
        GlacioConfiguration configuration = configurationClass.getAnnotation(GlacioConfiguration.class);
        if (configuration == null) {
            throw new GlacioRunnerInitializationException("Given configuration class[" +
                configurationClass.getSimpleName() + "] must be annotated with @" + GlacioConfiguration.class.getSimpleName());
        }
        Set<String> featurePaths = toSet(configuration.featurePaths());
        Set<String> gluePaths = toSet(configuration.gluePaths());

        return new ConfigurationContext(configurationClass, gluePaths, featurePaths);
    }

    private Set<String> toSet(String[] strings) {
        return stream(strings).collect(toCollection(LinkedHashSet::new));
    }
}
