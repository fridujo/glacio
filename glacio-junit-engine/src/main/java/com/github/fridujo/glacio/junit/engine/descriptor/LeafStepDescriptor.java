package com.github.fridujo.glacio.junit.engine.descriptor;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.ExecutionResult;
import com.github.fridujo.glacio.running.runtime.MissingStepImplementationException;
import com.github.fridujo.glacio.running.runtime.Status;
import com.github.fridujo.glacio.running.runtime.glue.AmbiguousStepDefinitionsException;
import com.github.fridujo.glacio.running.runtime.glue.Executable;
import org.junit.platform.engine.UniqueId;

public class LeafStepDescriptor extends GlacioTestDescriptor {
    private final UniqueId exampleId;
    private final Step step;

    public LeafStepDescriptor(UniqueId exampleId, UniqueId parentUniqueId, Step step) {
        super(parentUniqueId.append("step", step.getText()), step.getLine());
        this.exampleId = exampleId;
        this.step = step;
    }

    @Override
    public Type getType() {
        return Type.TEST;
    }

    @Override
    public GlacioEngineExecutionContext execute(GlacioEngineExecutionContext context, DynamicTestExecutor dynamicTestExecutor) throws Exception {
        context.skipIfPreviousStepFailed(exampleId, step);
        Executable executable;
        try {
            executable = context.getExecutableLookup().lookup(step);
            ExecutionResult executionResult = executable.execute();
            context.failStepIfNecessary(exampleId, executionResult);
        } catch (MissingStepImplementationException | AmbiguousStepDefinitionsException e) {
            context.failStepIfNecessary(exampleId, new ExecutionResult(Status.ABORT, e.getMessage()));
        }
        return context;
    }
}
