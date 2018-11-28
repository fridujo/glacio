package com.github.fridujo.glacio.junit.engine.descriptor;

import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.ExecutionResult;
import com.github.fridujo.glacio.running.runtime.Status;
import com.github.fridujo.glacio.running.runtime.glue.AmbiguousStepDefinitionsException;
import com.github.fridujo.glacio.running.runtime.glue.Executable;
import com.github.fridujo.glacio.running.runtime.glue.MissingStepImplementationException;

public class LeafStepDescriptor extends AbstractGlacioTestDescriptor {
    private final ExceptionConverter exceptionConverter = new ExceptionConverter();
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
    public GlacioEngineExecutionContext execute(GlacioEngineExecutionContext context,
                                                DynamicTestExecutor dynamicTestExecutor) {
        ExecutionResult executionResult;
        try {
            Executable executable = context.getExecutableLookup().lookup(step);
            executionResult = executable.execute();
        } catch (MissingStepImplementationException | AmbiguousStepDefinitionsException e) {
            executionResult = new ExecutionResult(Status.FAIL, e.getMessage());
        }

        if (executionResult.getStatus() != Status.SUCCESS) {
            context.registerFailure(exampleId, step);
            exceptionConverter.toException(executionResult);
        }
        return context;
    }

    @Override
    public SkipResult shouldBeSkipped(GlacioEngineExecutionContext context) {
        return context.shouldBeSkipped(exampleId);
    }
}
