package com.github.fridujo.glacio.junit.engine;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static java.util.Optional.empty;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.model.Example;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.model.Language;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.api.extension.AfterConfigurationCallback;
import com.github.fridujo.glacio.running.api.extension.BeforeConfigurationCallback;
import com.github.fridujo.glacio.running.api.extension.BeforeExampleCallback;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.runtime.configuration.ConfigurationContext;

public class ModelBuilder {

    public static UniqueId uniqueId() {
        return UniqueId.forEngine(UUID.randomUUID().toString());
    }

    public static Feature feature() {
        return new Feature(URI.create("file:test"), "test", new Language("test", "test", "test"), emptyList());
    }

    public static Example example() {
        return new Example(
            "test",
            singletonMap("k", "v"),
            emptyList(),
            emptySet()
        );
    }

    public static Step step() {
        return new Step(false, empty(), "test", empty(), emptyList());
    }

    public static TestConfigurationContext testConfigurationContext() {
        return new TestConfigurationContext();
    }

    public static class TestExtension implements BeforeConfigurationCallback, BeforeExampleCallback, AfterConfigurationCallback {

        public final AtomicInteger beforeConfigurationCounter = new AtomicInteger();
        public final AtomicInteger beforeExampleCounter = new AtomicInteger();
        public final AtomicInteger afterConfigurationCounter = new AtomicInteger();

        @Override
        public void beforeConfiguration(ExtensionContext context) {
            beforeConfigurationCounter.incrementAndGet();
        }

        @Override
        public void beforeExample(ExtensionContext context) {
            beforeExampleCounter.incrementAndGet();
        }

        @Override
        public void afterConfiguration(ExtensionContext context) {
            afterConfigurationCounter.incrementAndGet();
        }
    }

    public static class TestConfigurationContext {

        private final TestExtension extension = new TestExtension();
        private final ConfigurationContext configurationContext = new ConfigurationContext(
            String.class,
            emptySet(),
            emptySet(),
            singleton(extension)
        );

        public TestExtension getExtension() {
            return extension;
        }

        public ConfigurationContext configurationContext() {
            return configurationContext;
        }
    }
}
