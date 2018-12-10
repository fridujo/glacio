package com.github.fridujo.glacio.running.runtime.configuration;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.fridujo.glacio.running.api.extension.AfterAllCallback;
import com.github.fridujo.glacio.running.api.extension.BeforeAllCallback;
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
    private final Set<BeforeAllCallback> beforeAllCallbacks;
    private final Set<AfterAllCallback> afterAllCallbacks;

    public ConfigurationContext(Class<?> configurationClass,
                                Set<String> gluePaths,
                                Set<String> featurePaths,
                                Set<Extension> extensions) {
        this.configurationClass = configurationClass;
        this.gluePaths = gluePaths;
        this.featurePaths = featurePaths;
        parameterResolvers = extractSpecificExtensions(extensions, ParameterResolver.class);
        beforeAllCallbacks = extractSpecificExtensions(extensions, BeforeAllCallback.class);
        afterAllCallbacks = extractSpecificExtensions(extensions, AfterAllCallback.class);
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
        beforeAllCallbacks.forEach(bac -> {
            try {
                bac.beforeAll(extensionContext);
            } catch (Exception e) {
                logger.warn("Exception occurred in " + BeforeAllCallback.class.getSimpleName() + " (" + bac.getClass() + ")" + ": " + e.getMessage(), e);
            }
        });
    }

    public void afterConfiguration(ExtensionContext extensionContext) {
        afterAllCallbacks.forEach(bac -> {
            try {
                bac.afterAll(extensionContext);
            } catch (Exception e) {
                logger.warn("Exception occurred in " + AfterAllCallback.class.getSimpleName() + " (" + bac.getClass() + ")" + ": " + e.getMessage(), e);
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
}
