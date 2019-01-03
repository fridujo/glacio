package com.github.fridujo.glacio.running.runtime.configuration;

import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toCollection;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.fridujo.glacio.running.api.configuration.GlacioConfiguration;
import com.github.fridujo.glacio.running.api.extension.ExtendGlacioWith;
import com.github.fridujo.glacio.running.api.extension.Extension;
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
            runtimeOptions.getFeaturePaths(),
            emptySet()
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

        ExtendGlacioWith[] annotationsByType = configurationClass.getAnnotationsByType(ExtendGlacioWith.class);
        Set<Extension> extensions = stream(annotationsByType)
            .map(ExtendGlacioWith::value)
            .map(this::instantiate)
            .collect(Collectors.toSet());
        return new ConfigurationContext(configurationClass, gluePaths, featurePaths, extensions);
    }

    private Extension instantiate(Class<? extends Extension> extensionClass) {
        try {
            return extensionClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new GlacioRunnerInitializationException("Cannot instantiate " + Extension.class.getSimpleName() +
                " class " + extensionClass.getName(), e);
        }
    }

    private Set<String> toSet(String[] strings) {
        return stream(strings).collect(toCollection(LinkedHashSet::new));
    }
}
