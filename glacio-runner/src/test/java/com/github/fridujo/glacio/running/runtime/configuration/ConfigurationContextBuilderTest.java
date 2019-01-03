package com.github.fridujo.glacio.running.runtime.configuration;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.configuration.GlacioConfiguration;
import com.github.fridujo.glacio.running.api.extension.BeforeConfigurationCallback;
import com.github.fridujo.glacio.running.api.extension.BeforeExampleCallback;
import com.github.fridujo.glacio.running.api.extension.ExtendGlacioWith;
import com.github.fridujo.glacio.running.api.extension.Extension;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.runtime.GlacioRunnerInitializationException;
import com.github.fridujo.glacio.running.runtime.RuntimeOptions;
import com.github.fridujo.glacio.running.runtime.extension.ExtensionContextImpl;

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

        ExtensionContextImpl extensionContext = new ExtensionContextImpl(TestConfiguration.class);

        defaultConfigurationContext.beforeConfiguration(extensionContext);
        assertThat(extensionContext.getStore("test").<String>get("beforeConfiguration")).isEqualTo("beforeConfiguration");

        defaultConfigurationContext.beforeExample(extensionContext);
        assertThat(extensionContext.getStore("test").<String>get("beforeExample")).isEqualTo("beforeExample");
    }

    @Test
    void with_class_without_configuration_annotation() {
        RuntimeOptions runtimeOptions = new RuntimeOptions(singleton("features"), singleton("glues"), singleton(String.class));

        assertThatExceptionOfType(GlacioRunnerInitializationException.class)
            .isThrownBy(() -> new ConfigurationContextBuilder().fromRuntimeOptions(runtimeOptions))
            .withMessageContaining("Given configuration class[String] must be annotated with @GlacioConfiguration");
    }

    @Test
    void with_not_instantiable_extension() {
        RuntimeOptions runtimeOptions = new RuntimeOptions(singleton("features"), singleton("glues"), singleton(TestConfigurationWithNotInstantiableExtension.class));

        assertThatExceptionOfType(GlacioRunnerInitializationException.class)
            .isThrownBy(() -> new ConfigurationContextBuilder().fromRuntimeOptions(runtimeOptions))
            .withMessageContaining("Cannot instantiate Extension class com.github.fridujo.glacio.running.api.extension.Extension");
    }

    @GlacioConfiguration(featurePaths = "features-c", gluePaths = "glues-c")
    @ExtendGlacioWith(TestBeforeExample.class)
    private static final class TestConfiguration {
    }

    @GlacioConfiguration
    @ExtendGlacioWith(Extension.class)
    private static final class TestConfigurationWithNotInstantiableExtension {
    }

    public static final class TestBeforeExample implements BeforeConfigurationCallback, BeforeExampleCallback {

        @Override
        public void beforeExample(ExtensionContext context) {
            context.getStore("test").getOrComputeIfAbsent("beforeExample", Function.identity(), String.class);
        }

        @Override
        public void beforeConfiguration(ExtensionContext context) {
            context.getStore("test").getOrComputeIfAbsent("beforeConfiguration", Function.identity(), String.class);
        }
    }
}
