package com.github.fridujo.glacio.running.api.convert;

import static com.github.fridujo.glacio.running.api.convert.Primitives.wrapperOf;

import java.lang.reflect.Executable;

public class ParameterDescriptor {
    public final int position;
    public final Class<?> type;
    public final Executable executable;

    public ParameterDescriptor(int position, Class<?> type, Executable executable) {
        this.position = position;
        this.type = wrapperOf(type);
        this.executable = executable;
    }

    @Override
    public String toString() {
        return "Parameter(" +
            "position=" + position +
            ", type=" + type +
            ") of " + executable;
    }
}
