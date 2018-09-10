package com.github.fridujo.glacio.junit.engine;

import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.ExecutionResult;
import com.github.fridujo.glacio.running.runtime.Status;
import com.github.fridujo.glacio.running.runtime.event.EventAware;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;
import org.opentest4j.TestSkippedException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlacioEngineExecutionContext implements EngineExecutionContext {

    private final ExecutableLookup executableLookup;
    private final EventAware eventAware;
    private final Map<UniqueId, ExecutionResult> lastResultsByExampleId = new ConcurrentHashMap<>();

    public GlacioEngineExecutionContext(ExecutableLookup executableLookup, EventAware eventAware) {
        this.executableLookup = executableLookup;
        this.eventAware = eventAware;
        eventAware.beforeInitialization();
        eventAware.beforeAllFeatures();
    }

    public ExecutableLookup getExecutableLookup() {
        return executableLookup;
    }

    public EventAware getEventAware() {
        return eventAware;
    }

    public void failStepIfNecessary(UniqueId exampleId, ExecutionResult executionResult) throws Exception {
        lastResultsByExampleId.put(exampleId, executionResult);
        Status status = executionResult.getStatus();
        Throwable cause = executionResult.getCause();
        if (cause != null) {
            if (cause instanceof Error) {
                throw (Error) cause;
            } else if (cause instanceof Exception) {
                throw (Exception) cause;
            } else {
                throw new TestAbortedException("Unexpected Throwable", cause);
            }
        }
        if (status == Status.ABORT) {
            throw new TestAbortedException(executionResult.getMessage());
        } else if (status == Status.FAIL) {
            throw new AssertionFailedError(executionResult.getMessage());
        }
    }

    public void skipIfPreviousStepFailed(UniqueId exampleId, Step step) {
        ExecutionResult lastExecutionResult = lastResultsByExampleId.get(exampleId);
        if (lastExecutionResult != null && lastExecutionResult.getStatus() != Status.SUCCESS) {
            throw new TestSkippedException("Skipping step " + step.getLine());
        }
    }
}
