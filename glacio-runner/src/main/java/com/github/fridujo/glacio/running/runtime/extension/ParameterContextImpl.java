package com.github.fridujo.glacio.running.runtime.extension;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

import com.github.fridujo.glacio.running.api.extension.ParameterContext;

public class ParameterContextImpl implements ParameterContext {
    private final Executable declaringExecutable;
    private final Parameter parameter;
    private final int index;

    public ParameterContextImpl(Executable declaringExecutable, Parameter parameter, int index) {
        this.declaringExecutable = declaringExecutable;
        this.parameter = parameter;
        this.index = index;
    }

    @Override
    public Executable getDeclaringExecutable() {
        return declaringExecutable;
    }

    @Override
    public Parameter getParameter() {
        return parameter;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return simpleName(declaringExecutable) + "([" + index + "] " +
            parameter.getType().getSimpleName() + " " + parameter.getName() + ")";
    }

    private String simpleName(Executable executable) {
        String name = executable.getName();
        int lastDotIndex = name.lastIndexOf('.');
        final String simpleName;
        if (lastDotIndex != -1) {
            simpleName = name.substring(lastDotIndex + 1);
        } else {
            simpleName = name;
        }
        return simpleName;
    }
}
