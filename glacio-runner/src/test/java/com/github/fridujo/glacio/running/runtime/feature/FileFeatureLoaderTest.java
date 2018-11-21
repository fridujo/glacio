package com.github.fridujo.glacio.running.runtime.feature;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.running.runtime.GlacioRunnerInitializationException;
import com.github.fridujo.glacio.running.runtime.io.GlacioIOException;
import com.github.fridujo.glacio.running.runtime.io.ResourceLoader;

class FileFeatureLoaderTest {

    @Test
    void load_empty_feature_set() {
        ResourceLoader resourceLoader = mock(ResourceLoader.class);
        FeatureLoader featureLoader = new FileFeatureLoader(resourceLoader);

        Set<Feature> featureSet = featureLoader.load(emptySet());

        assertThat(featureSet).isEmpty();
    }

    @Test
    void load_valid_feature_set() {
        ResourceLoader resourceLoader = mock(ResourceLoader.class);
        when(resourceLoader.resources(any(), any()))
            .thenReturn(singleton(TestResource.build().withContent("Feature: test")));
        FeatureLoader featureLoader = new FileFeatureLoader(resourceLoader);

        Set<Feature> featureSet = featureLoader.load(singleton("test"));

        assertThat(featureSet).hasSize(1);
        assertThat(featureSet.iterator().next().getName())
            .as("Feature name")
            .isEqualTo("test");
    }

    @Test
    void load_invalid_feature_set() {
        ResourceLoader resourceLoader = mock(ResourceLoader.class);
        when(resourceLoader.resources(any(), any()))
            .thenReturn(singleton(TestResource.build().withContent("invalid feature content").withAbsolutePath("test.feature")));
        FeatureLoader featureLoader = new FileFeatureLoader(resourceLoader);

        assertThatExceptionOfType(GlacioRunnerInitializationException.class)
            .isThrownBy(() -> featureLoader.load(singleton("test")))
            .withMessage("Cannot parse feature file: test.feature");
    }

    @Test
    void load_unloadable_resource() {
        ResourceLoader resourceLoader = mock(ResourceLoader.class);
        when(resourceLoader.resources(any(), any()))
            .thenReturn(singleton(TestResource.build().throwing().withAbsolutePath("test.feature")));
        FeatureLoader featureLoader = new FileFeatureLoader(resourceLoader);

        assertThatExceptionOfType(GlacioIOException.class)
            .isThrownBy(() -> featureLoader.load(singleton("test")))
            .withMessage("Cannot read from feature file: test.feature");
    }
}
