package com.github.fridujo.glacio.running.runtime.configuration;

import java.util.Optional;
import java.util.Set;

import com.github.fridujo.glacio.running.logging.Logger;
import com.github.fridujo.glacio.running.logging.LoggerFactory;

public class ConfigurationContext {

    private final Logger logger = LoggerFactory.getLogger(ConfigurationContext.class);
    private final Class<?> configurationClass;
    private final Set<String> gluePaths;
    private final Set<String> featurePaths;

    public ConfigurationContext(Class<?> configurationClass,
                                Set<String> gluePaths,
                                Set<String> featurePaths) {
        this.configurationClass = configurationClass;
        this.gluePaths = gluePaths;
        this.featurePaths = featurePaths;
    }

    public Class<?> getConfigurationClass() {
        return configurationClass;
    }

    public Set<String> getGluePaths() {
        return gluePaths;
    }

    public Set<String> getFeaturePaths() {
        return featurePaths;
    }

    public String name() {
        return Optional.ofNullable(configurationClass)
            .map(Class::getSimpleName)
            .orElse("default");
    }

    public ClassLoader getClassLoader() {
        return Optional.ofNullable(configurationClass)
            .map(Class::getClassLoader)
            .orElse(Thread.currentThread().getContextClassLoader());
    }
}
