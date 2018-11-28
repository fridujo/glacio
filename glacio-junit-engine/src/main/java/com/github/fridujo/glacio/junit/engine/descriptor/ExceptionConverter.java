package com.github.fridujo.glacio.junit.engine.descriptor;

import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

import com.github.fridujo.glacio.running.runtime.ExecutionResult;
import com.github.fridujo.glacio.running.runtime.Status;

public class ExceptionConverter {

    public void toException(ExecutionResult executionResult) {
        Status status = executionResult.getStatus();
        Throwable cause = executionResult.getCause();
        if (cause instanceof Error && cause.getClass() != AssertionError.class) {
            throw (Error) cause;
        }

        if (status == Status.ABORT) {
            throw new TestAbortedException(executionResult.getMessage(), executionResult.getCause());
        } else if (status == Status.FAIL) {
            throw new AssertionFailedError(executionResult.getMessage(), executionResult.getCause());
        } else if (cause != null) {
            throw new AssertionFailedError("Unexpected Throwable", cause);
        }
    }
}
