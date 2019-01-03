package com.github.fridujo.glacio.junit.engine.descriptor;

import static com.github.fridujo.glacio.junit.engine.ModelBuilder.example;
import static com.github.fridujo.glacio.junit.engine.ModelBuilder.feature;
import static com.github.fridujo.glacio.junit.engine.ModelBuilder.step;
import static com.github.fridujo.glacio.junit.engine.ModelBuilder.testConfigurationContext;
import static com.github.fridujo.glacio.junit.engine.ModelBuilder.uniqueId;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.junit.engine.ModelBuilder;

class DescriptorsTests {

    static List<TestDescriptor> containerDescriptors() {
        return Arrays.asList(
            new GlacioEngineDescriptor(uniqueId()),
            new GlacioConfigurationDescriptor(uniqueId(), testConfigurationContext().configurationContext()),
            new FeatureDescriptor(uniqueId(), uniqueId(), feature()),
            new ExampleDescriptor(uniqueId(), uniqueId(), example()),
            new StepContainerDescriptor(uniqueId(), uniqueId(), uniqueId(), step())
        );
    }

    @ParameterizedTest
    @MethodSource("containerDescriptors")
    void containerDescriptors_are_of_container_type(TestDescriptor testDescriptor) {
        assertThat(testDescriptor.getType()).isEqualTo(TestDescriptor.Type.CONTAINER);
    }

    @Test
    void leafStepExecutor_is_of_test_type() {
        LeafStepDescriptor descriptor = new LeafStepDescriptor(uniqueId(), uniqueId(), uniqueId(), step());
        assertThat(descriptor.getType()).isEqualTo(TestDescriptor.Type.TEST);
    }

    @Test
    void exampleDescriptor_displayName_contains_parameters() {
        ExampleDescriptor exampleDescriptor = new ExampleDescriptor(uniqueId(), uniqueId(), example());

        assertThat(exampleDescriptor.getDisplayName()).isEqualTo("Example: test (k=v)");
    }

    @Test
    void exampleDescriptor_invoke_cleanup_after_execution() {
        ExampleDescriptor exampleDescriptor = new ExampleDescriptor(uniqueId(), uniqueId(), example());
        GlacioEngineExecutionContext context = new GlacioEngineExecutionContext(
        );
        UniqueId exampleId = exampleDescriptor.getUniqueId();
        context.registerFailure(exampleId, step());
        assertThat(context.shouldBeSkipped(exampleId).isSkipped()).isTrue();

        exampleDescriptor.cleanUp(context);

        assertThat(context.shouldBeSkipped(exampleId).isSkipped()).isFalse();
    }

    @Test
    void prepare_creates_an_executable_lookup() {
        ModelBuilder.TestConfigurationContext configurationContext = testConfigurationContext();
        GlacioConfigurationDescriptor glacioConfigurationDescriptor = new GlacioConfigurationDescriptor(uniqueId(), configurationContext.configurationContext());

        GlacioEngineExecutionContext context = new GlacioEngineExecutionContext();
        glacioConfigurationDescriptor.prepare(context);

        assertThat(context.getExecutableLookup(glacioConfigurationDescriptor.getUniqueId())).isNotNull();
    }

    @Test
    void configurationDescriptor_invoke_before_callbacks_before_execution() {
        ModelBuilder.TestConfigurationContext configurationContext = testConfigurationContext();
        GlacioConfigurationDescriptor glacioConfigurationDescriptor = new GlacioConfigurationDescriptor(uniqueId(), configurationContext.configurationContext());

        glacioConfigurationDescriptor.before(new GlacioEngineExecutionContext());

        assertThat(configurationContext.getExtension().beforeConfigurationCounter).hasValue(1);
    }

    @Test
    void exampleDescriptor_notify_beforeExample_before_execution() {
        ModelBuilder.TestConfigurationContext configurationContext = testConfigurationContext();
        GlacioConfigurationDescriptor glacioConfigurationDescriptor = new GlacioConfigurationDescriptor(uniqueId(), configurationContext.configurationContext());
        GlacioEngineExecutionContext context = new GlacioEngineExecutionContext();
        glacioConfigurationDescriptor.prepare(context);
        ExampleDescriptor exampleDescriptor = new ExampleDescriptor(glacioConfigurationDescriptor.getUniqueId(), uniqueId(), example());

        exampleDescriptor.prepare(context);

        assertThat(configurationContext.getExtension().beforeExampleCounter).hasValue(1);
    }

    @Test
    void configurationDescriptor_invoke_after_callbacks_after_execution() {
        ModelBuilder.TestConfigurationContext configurationContext = testConfigurationContext();
        GlacioConfigurationDescriptor glacioConfigurationDescriptor = new GlacioConfigurationDescriptor(uniqueId(), configurationContext.configurationContext());

        glacioConfigurationDescriptor.after(new GlacioEngineExecutionContext());

        assertThat(configurationContext.getExtension().afterConfigurationCounter).hasValue(1);
    }
}
