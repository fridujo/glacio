package com.github.fridujo.glacio.running.api.convert;

import static com.github.fridujo.glacio.running.api.convert.Primitives.wrapperOf;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ParameterDescriptor {
    public final int position;
    public final Class<?> type;
    public final ParameterizedType parameterizedType;
    public final Executable executable;
    private final List<Annotation> annotations;

    public ParameterDescriptor(int position, Class<?> type, Type genericType, List<Annotation> annotations, Executable executable) {
        this.position = position;
        this.type = wrapperOf(type);
        this.parameterizedType = genericType instanceof ParameterizedType ? (ParameterizedType) genericType : null;
        this.annotations = annotations;
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

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        Set<Annotation> visited = new LinkedHashSet<>();
        return getAnnotation(annotationClass, annotations, visited);
    }

    private <T extends Annotation> T getAnnotation(Class<T> annotationClass, List<Annotation> annotationList, Set<Annotation> visited) {
        for (Annotation annotation : annotationList) {
            if (visited.contains(annotation)) {
                continue;
            }
            visited.add(annotation);
            if (annotation.annotationType().equals(annotationClass)) {
                return (T) annotation;
            }
            Annotation inHierarchy = getAnnotation(annotationClass, Arrays.asList(annotation.annotationType().getAnnotations()), visited);
            if (inHierarchy != null) {
                return (T) inHierarchy;
            }
        }
        return null;
    }
}
