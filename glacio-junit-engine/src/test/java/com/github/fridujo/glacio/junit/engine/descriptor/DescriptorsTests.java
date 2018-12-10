package com.github.fridujo.glacio.junit.engine.descriptor;

import static com.github.fridujo.glacio.junit.engine.ModelBuilder.example;
import static com.github.fridujo.glacio.junit.engine.ModelBuilder.feature;
import static com.github.fridujo.glacio.junit.engine.ModelBuilder.step;
import static com.github.fridujo.glacio.junit.engine.ModelBuilder.uniqueId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.BeforeExampleEventAware;
import com.github.fridujo.glacio.running.runtime.glue.AmbiguousStepDefinitionsException;
import com.github.fridujo.glacio.running.runtime.glue.Executable;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;
import com.github.fridujo.glacio.running.runtime.glue.MissingStepImplementationException;

class DescriptorsTests {

    static List<TestDescriptor> containerDescriptors() {
        return Arrays.asList(
            new GlacioEngineDescriptor(uniqueId()),
            new FeatureDescriptor(uniqueId(), feature()),
            new ExampleDescriptor(uniqueId(), example()),
            new StepContainerDescriptor(uniqueId(), uniqueId(), step())
        );
    }

    @ParameterizedTest
    @MethodSource("containerDescriptors")
    void containerDescriptors_are_of_container_type(TestDescriptor testDescriptor) {
        assertThat(testDescriptor.getType()).isEqualTo(TestDescriptor.Type.CONTAINER);
    }

    @Test
    void leafStepExecutor_is_of_test_type() {
        LeafStepDescriptor descriptor = new LeafStepDescriptor(uniqueId(), uniqueId(), step());
        assertThat(descriptor.getType()).isEqualTo(TestDescriptor.Type.TEST);
    }

    @Test
    void exampleDescriptor_displayName_contains_parameters() {
        ExampleDescriptor exampleDescriptor = new ExampleDescriptor(uniqueId(), example());

        assertThat(exampleDescriptor.getDisplayName()).isEqualTo("Example: test (k=v)");
    }

    @Test
    void exampleDescriptor_invoke_beforeExampleEventAware_before_execution() {
        ExampleDescriptor exampleDescriptor = new ExampleDescriptor(uniqueId(), example());
        TestExecutableLookup executableLookup = new TestExecutableLookup();
        GlacioEngineExecutionContext context = new GlacioEngineExecutionContext();
        context.setExecutableLookup(executableLookup);

        exampleDescriptor.prepare(context);

        verify(executableLookup.beforeExampleEventAware, times(1)).beforeExample();
    }

    @Test
    void exampleDescriptor_invoke_cleanup_after_execution() {
        ExampleDescriptor exampleDescriptor = new ExampleDescriptor(uniqueId(), example());
        GlacioEngineExecutionContext context = new GlacioEngineExecutionContext(
        );
        UniqueId exampleId = exampleDescriptor.getUniqueId();
        context.registerFailure(exampleId, step());
        assertThat(context.shouldBeSkipped(exampleId).isSkipped()).isTrue();

        exampleDescriptor.cleanUp(context);

        assertThat(context.shouldBeSkipped(exampleId).isSkipped()).isFalse();

    }

    private static final class TestExecutableLookup implements ExecutableLookup, BeforeExampleEventAware {

        private final BeforeExampleEventAware beforeExampleEventAware = mock(BeforeExampleEventAware.class);

        @Override
        public void beforeExample() {
            beforeExampleEventAware.beforeExample();
        }

        @Override
        public Executable lookup(Step step) throws MissingStepImplementationException, AmbiguousStepDefinitionsException {
            return null;
        }
    }
}
