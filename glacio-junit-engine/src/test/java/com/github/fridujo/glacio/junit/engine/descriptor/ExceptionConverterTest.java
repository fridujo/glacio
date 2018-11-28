package com.github.fridujo.glacio.junit.engine.descriptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

import com.github.fridujo.glacio.running.runtime.ExecutionResult;
import com.github.fridujo.glacio.running.runtime.Status;

class ExceptionConverterTest {

    private final ExceptionConverter exceptionConverter = new ExceptionConverter();

    @Test
    void error_are_rethrown() {
        ExecutionResult executionResult = new ExecutionResult(Status.FAIL, "test", new OutOfMemoryError());

        assertThatExceptionOfType(OutOfMemoryError.class)
            .isThrownBy(() -> exceptionConverter.toException(executionResult));
    }

    @Test
    void abort_status_throws_TestAbortedException() {
        ExecutionResult executionResult = new ExecutionResult(Status.ABORT, "test");
        assertThatExceptionOfType(TestAbortedException.class)
            .isThrownBy(() -> exceptionConverter.toException(executionResult))
            .withMessage("test");
    }

    @Test
    void incoherent_case_throws() {
        RuntimeException cause = new RuntimeException("test");
        ExecutionResult executionResult = new ExecutionResult(Status.SUCCESS, "OK", cause);

        assertThatExceptionOfType(AssertionFailedError.class)
            .isThrownBy(() -> exceptionConverter.toException(executionResult))
            .withMessage("Unexpected Throwable")
            .withCause(cause);
    }

    @Test
    void nothing_is_thrown_when_there_is_no_error() {
        ExecutionResult executionResult = new ExecutionResult(Status.SUCCESS, "OK");

        exceptionConverter.toException(executionResult);

        assertThat(true).as("no exception thrown").isTrue();
    }
}
