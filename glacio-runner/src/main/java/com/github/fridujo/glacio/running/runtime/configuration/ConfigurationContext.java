package com.github.fridujo.glacio.running.runtime.configuration;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.fridujo.glacio.running.api.extension.AfterConfigurationCallback;
import com.github.fridujo.glacio.running.api.extension.BeforeConfigurationCallback;
import com.github.fridujo.glacio.running.api.extension.BeforeExampleCallback;
import com.github.fridujo.glacio.running.api.extension.Extension;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.api.extension.ParameterResolver;
import com.github.fridujo.glacio.running.logging.Logger;
import com.github.fridujo.glacio.running.logging.LoggerFactory;

public class ConfigurationContext {

    private final Logger logger = LoggerFactory.getLogger(ConfigurationContext.class);
    private final Class<?> configurationClass;
    private final Set<String> gluePaths;
    private final Set<String> featurePaths;
    private final Set<ParameterResolver> parameterResolvers;
    private final Set<BeforeConfigurationCallback> beforeConfigurationCallbacks;
    private final Set<BeforeExampleCallback> beforeExampleCallbacks;
    private final Set<AfterConfigurationCallback> afterConfigurationCallbacks;

    public ConfigurationContext(Class<?> configurationClass,
                                Set<String> gluePaths,
                                Set<String> featurePaths,
                                Set<Extension> extensions) {
        this.configurationClass = configurationClass;
        this.gluePaths = gluePaths;
        this.featurePaths = featurePaths;
        parameterResolvers = extractSpecificExtensions(extensions, ParameterResolver.class);
        beforeConfigurationCallbacks = extractSpecificExtensions(extensions, BeforeConfigurationCallback.class);
        beforeExampleCallbacks = extractSpecificExtensions(extensions, BeforeExampleCallback.class);
        afterConfigurationCallbacks = extractSpecificExtensions(extensions, AfterConfigurationCallback.class);
    }

    private <T extends Extension> Set<T> extractSpecificExtensions(Set<Extension> extensions, Class<T> extensionClass) {
        return extensions.stream()
            .filter(e -> extensionClass.isAssignableFrom(e.getClass()))
            .map(extensionClass::cast)
            .collect(Collectors.toSet());
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

    public Set<ParameterResolver> getParameterResolvers() {
        return parameterResolvers;
    }

    public void beforeConfiguration(ExtensionContext extensionContext) {
        beforeConfigurationCallbacks.forEach(bac -> {
            try {
                bac.beforeConfiguration(extensionContext);
            } catch (Exception e) {
                logger.warn("Exception occurred in " + BeforeConfigurationCallback.class.getSimpleName() + " (" + bac.getClass() + ")" + ": " + e.getMessage(), e);
            }
        });
    }

    public void beforeExample(ExtensionContext extensionContext) {
        beforeExampleCallbacks.forEach(bac -> {
            try {
                bac.beforeExample(extensionContext);
            } catch (Exception e) {
                logger.warn("Exception occurred in " + BeforeExampleCallback.class.getSimpleName() + " (" + bac.getClass() + ")" + ": " + e.getMessage(), e);
            }
        });
    }

    public void afterConfiguration(ExtensionContext extensionContext) {
        afterConfigurationCallbacks.forEach(bac -> {
            try {
                bac.afterConfiguration(extensionContext);
            } catch (Exception e) {
                logger.warn("Exception occurred in " + AfterConfigurationCallback.class.getSimpleName() + " (" + bac.getClass() + ")" + ": " + e.getMessage(), e);
            }
        });
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

    public void enrichWith(BeforeExampleCallback beforeExampleCallback) {
        beforeExampleCallbacks.add(beforeExampleCallback);
    }
}
