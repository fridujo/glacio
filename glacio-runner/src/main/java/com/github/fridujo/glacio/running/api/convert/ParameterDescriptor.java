package com.github.fridujo.glacio.running.api.convert;

import static com.github.fridujo.glacio.running.api.convert.Primitives.wrapperOf;

import java.lang.reflect.Executable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterDescriptor {
    public final int position;
    public final Class<?> type;
    public final ParameterizedType parameterizedType;
    public final Executable executable;

    public ParameterDescriptor(int position, Class<?> type, Type genericType, Executable executable) {
        this.position = position;
        this.type = wrapperOf(type);
        this.parameterizedType = genericType instanceof ParameterizedType ? (ParameterizedType) genericType : null;
        this.executable = executable;
    }

    @Override
    public String toString() {
        return "Parameter(" +
            "position=" + position +
            ", type=" + type +
            ") of " + executable;
    }

    public Class<?> getTypeArgument(int position) {
        return parameterizedType != null && parameterizedType.getActualTypeArguments().length > position
            ? toClass(parameterizedType.getActualTypeArguments()[position]) : null;
    }

    private Class<?> toClass(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        } else {
            return (Class) type;
        }
    }
}
