package com.github.fridujo.glacio.running.runtime.glue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.ExecutionResult;
import com.github.fridujo.glacio.running.runtime.Status;
import com.github.fridujo.glacio.running.runtime.convert.MethodParameterConverter;

class JavaExecutable implements Executable {

    private final GlueFactory glueFactory;
    private final MethodParameterConverter methodParameterConverter;
    private final Step step;
    private final Pattern pattern;
    private final Method method;

    JavaExecutable(GlueFactory glueFactory, MethodParameterConverter methodParameterConverter, Step step, Pattern pattern, Method method) {
        this.glueFactory = glueFactory;
        this.methodParameterConverter = methodParameterConverter;
        this.step = step;
        this.pattern = pattern;
        this.method = method;
    }

    @Override
    public ExecutionResult execute() {
        Object[] rawParameters = extractParameters(step);
        Object glue = glueFactory.getGlue(method.getDeclaringClass());
        Object[] parameters = methodParameterConverter.convert(rawParameters, method);
        try {
            method.invoke(glue, parameters);
            return new ExecutionResult(Status.SUCCESS);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            return new ExecutionResult(Status.ABORT, e.getMessage(), e);
        } catch (InvocationTargetException e) {
            return new ExecutionResult(Status.FAIL, e.getTargetException().getMessage(), e.getTargetException());
        }
    }

    private Object[] extractParameters(Step step) {
        int parameterCount = 0;
        Matcher matcher = pattern.matcher(step.getText());
        matcher.matches();
        int groupCount = matcher.groupCount();
        parameterCount += groupCount;
        if (step.getArgument().isPresent()) {
            parameterCount += 1;
        }
        Object[] parameters = new Object[parameterCount];
        if (groupCount > 0) {
            IntStream.rangeClosed(1, groupCount).forEach(i -> parameters[i - 1] = matcher.group(i));
        }
        if (step.getArgument().isPresent()) {
            parameters[parameterCount - 1] = step.getArgument().get();
        }
        return parameters;
    }
}
