package com.github.fridujo.glacio.running.runtime.configuration;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.extension.BeforeConfigurationCallback;
import com.github.fridujo.glacio.running.api.extension.BeforeExampleCallback;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;

class ConfigurationContextTest {

    @Test
    void exception_in_before_configuration_callback_is_not_propagated() {
        BeforeConfigurationCallback beforeAllCallback = c -> {throw new IllegalStateException();};
        ConfigurationContext configurationContext = new ConfigurationContext(String.class, emptySet(), emptySet(), singleton(beforeAllCallback));

        configurationContext.beforeConfiguration(mock(ExtensionContext.class));

        assertThat(true).as("no exception thrown").isTrue();
    }

    @Test
    void exception_in_before_example_callback_is_not_propagated() {
        BeforeExampleCallback beforeExampleCallback = c -> {throw new IllegalStateException();};
        ConfigurationContext configurationContext = new ConfigurationContext(String.class, emptySet(), emptySet(), singleton(beforeExampleCallback));

        configurationContext.beforeExample(mock(ExtensionContext.class));

        assertThat(true).as("no exception thrown").isTrue();
    }
}
