package com.github.fridujo.glacio.running.runtime.extension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.extension.ExtensionContext;

class ExtensionContextTest {

    @Test
    void configuration_class_can_be_accessed() {
        ExtensionContext extensionContext = new ExtensionContextImpl(String.class);

        assertThat(extensionContext.getConfigurationClass()).isEqualTo(String.class);
    }

    @Test
    void store_can_hold_multiple_values_by_namespace() {
        ExtensionContext extensionContext = new ExtensionContextImpl(null);

        extensionContext.getStore("test").getOrComputeIfAbsent("test", k -> "test", String.class);
        assertThat(extensionContext.getStore("test").<String>get("test")).isEqualTo("test");
        assertThat(extensionContext.getStore("test2").<String>get("test")).isNull();
    }

    @Test
    void value_can_be_removed_from_store() {
        ExtensionContext extensionContext = new ExtensionContextImpl(null);

        extensionContext.getStore("test").getOrComputeIfAbsent("test", k -> "test", String.class);

        extensionContext.getStore("test").remove("test");
        assertThat(extensionContext.getStore("test").<String>get("test")).isNull();
    }

    @Test
    void store_toString_display_namespace() {
        String namespace = UUID.randomUUID().toString();
        ExtensionContext extensionContext = new ExtensionContextImpl(null);

        assertThat(extensionContext.getStore(namespace)).hasToString(namespace + " Store");
    }
}
