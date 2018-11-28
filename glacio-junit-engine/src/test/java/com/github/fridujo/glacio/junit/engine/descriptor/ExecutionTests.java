package com.github.fridujo.glacio.junit.engine.descriptor;

import static com.github.fridujo.glacio.junit.engine.ModelBuilder.step;
import static com.github.fridujo.glacio.junit.engine.ModelBuilder.uniqueId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.Node;
import org.opentest4j.AssertionFailedError;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.running.GlacioRunnerException;
import com.github.fridujo.glacio.running.runtime.BeforeExampleEventAware;
import com.github.fridujo.glacio.running.runtime.ExecutionResult;
import com.github.fridujo.glacio.running.runtime.Status;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;
import com.github.fridujo.glacio.running.runtime.glue.MissingStepImplementationException;

class ExecutionTests {

    static List<GlacioRunnerException> lookup_exceptions() {
        return Arrays.asList(
            new MissingStepImplementationException(step())
        );
    }

    @ParameterizedTest
    @MethodSource("lookup_exceptions")
    void failed_lookup(GlacioRunnerException lookupException) {
        Node<GlacioEngineExecutionContext> testDescriptor = new LeafStepDescriptor(uniqueId(), uniqueId(), step());
        ExecutableLookup mock = mock(ExecutableLookup.class, RETURNS_DEEP_STUBS);
        GlacioEngineExecutionContext context = new GlacioEngineExecutionContext(
            mock,
            mock(BeforeExampleEventAware.class));
        when(mock.lookup(any())).thenThrow(lookupException);

        assertThatExceptionOfType(AssertionFailedError.class)
            .isThrownBy(() -> testDescriptor.execute(context, null))
            .withMessage(lookupException.getMessage());
    }

    @Test
    void failed_execution() {
        Node<GlacioEngineExecutionContext> testDescriptor = new LeafStepDescriptor(uniqueId(), uniqueId(), step());
        ExecutableLookup mock = mock(ExecutableLookup.class, RETURNS_DEEP_STUBS);
        GlacioEngineExecutionContext context = new GlacioEngineExecutionContext(
            mock,
            mock(BeforeExampleEventAware.class));
        String failedAssertionMessage = "a should be equal to b";
        when(mock.lookup(any()).execute()).thenReturn(new ExecutionResult(Status.FAIL, failedAssertionMessage, new AssertionError(failedAssertionMessage)));

        assertThatExceptionOfType(AssertionFailedError.class)
            .isThrownBy(() -> testDescriptor.execute(context, null))
            .withMessage(failedAssertionMessage);
    }

    @Test
    void successful_execution() throws Exception {
        Node<GlacioEngineExecutionContext> testDescriptor = new LeafStepDescriptor(uniqueId(), uniqueId(), step());
        ExecutableLookup mock = mock(ExecutableLookup.class, RETURNS_DEEP_STUBS);
        GlacioEngineExecutionContext context = new GlacioEngineExecutionContext(
            mock,
            mock(BeforeExampleEventAware.class));
        when(mock.lookup(any()).execute()).thenReturn(new ExecutionResult(Status.SUCCESS));

        GlacioEngineExecutionContext resultingContext = testDescriptor.execute(context, null);

        assertThat(resultingContext).isSameAs(context);
        verify(mock.lookup(any()), times(1)).execute();
    }

    @Test
    void skip_execution_according_to_context() throws Exception {
        UniqueId exampleId = uniqueId();
        Node<GlacioEngineExecutionContext> testDescriptor = new LeafStepDescriptor(exampleId, uniqueId(), step());
        GlacioEngineExecutionContext context = new GlacioEngineExecutionContext(null, null);
        assertThat(testDescriptor.shouldBeSkipped(context).isSkipped()).isFalse();

        context.registerFailure(exampleId, step());

        assertThat(testDescriptor.shouldBeSkipped(context).isSkipped()).isTrue();
    }
}
