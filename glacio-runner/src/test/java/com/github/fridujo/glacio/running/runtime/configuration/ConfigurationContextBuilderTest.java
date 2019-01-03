package com.github.fridujo.glacio.running.runtime.configuration;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.configuration.GlacioConfiguration;
import com.github.fridujo.glacio.running.runtime.GlacioRunnerInitializationException;
import com.github.fridujo.glacio.running.runtime.RuntimeOptions;

class ConfigurationContextBuilderTest {

    @Test
    void without_configuration() {
        RuntimeOptions runtimeOptions = new RuntimeOptions(singleton("features"), singleton("glues"), emptySet());
        Set<ConfigurationContext> configurationContexts = new ConfigurationContextBuilder().fromRuntimeOptions(runtimeOptions);

        assertThat(configurationContexts).hasSize(1);
        ConfigurationContext defaultConfigurationContext = configurationContexts.iterator().next();

        assertThat(defaultConfigurationContext.name()).isEqualTo("default");
        assertThat(defaultConfigurationContext.getClassLoader()).isEqualTo(Thread.currentThread().getContextClassLoader());
        assertThat(defaultConfigurationContext.getConfigurationClass()).isNull();
        assertThat(defaultConfigurationContext.getFeaturePaths()).containsExactly("features");
        assertThat(defaultConfigurationContext.getGluePaths()).containsExactly("glues");
    }

    @Test
    void with_configuration_class() {
        RuntimeOptions runtimeOptions = new RuntimeOptions(singleton("features"), singleton("glues"), singleton(TestConfiguration.class));
        Set<ConfigurationContext> configurationContexts = new ConfigurationContextBuilder().fromRuntimeOptions(runtimeOptions);

        assertThat(configurationContexts).hasSize(1);
        ConfigurationContext defaultConfigurationContext = configurationContexts.iterator().next();

        assertThat(defaultConfigurationContext.name()).isEqualTo("TestConfiguration");
        assertThat(defaultConfigurationContext.getClassLoader()).isEqualTo(TestConfiguration.class.getClassLoader());
        assertThat(defaultConfigurationContext.getConfigurationClass()).isEqualTo(TestConfiguration.class);
        assertThat(defaultConfigurationContext.getFeaturePaths()).containsExactly("features-c");
        assertThat(defaultConfigurationContext.getGluePaths()).containsExactly("glues-c");
    }

    @Test
    void with_class_without_configuration_annotation() {
        RuntimeOptions runtimeOptions = new RuntimeOptions(singleton("features"), singleton("glues"), singleton(String.class));

        assertThatExceptionOfType(GlacioRunnerInitializationException.class)
            .isThrownBy(() -> new ConfigurationContextBuilder().fromRuntimeOptions(runtimeOptions))
            .withMessageContaining("Given configuration class[String] must be annotated with @GlacioConfiguration");
    }

    @GlacioConfiguration(featurePaths = "features-c", gluePaths = "glues-c")
    private static final class TestConfiguration {
    }
}
