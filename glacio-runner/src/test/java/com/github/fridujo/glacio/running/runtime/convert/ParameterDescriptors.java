package com.github.fridujo.glacio.running.runtime.convert;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;

class ParameterDescriptors {

    static ParameterDescriptor descriptor(Class<?> clazz) {
        return descriptor(0, clazz);
    }

    static ParameterDescriptor descriptor(int position, Class<?> clazz) {
        return new ParameterDescriptor(position, clazz, clazz, null);
    }

    static ParameterDescriptor descriptor(TypeReference typeReference) {
        return new ParameterDescriptor(0, typeReference.rawType(), typeReference.type, null);
    }

    static ParameterDescriptor descriptorForMethodArgument(Class<?> clazz, String methodName, int argumentPosition) {
        Method method = Arrays.stream(clazz.getDeclaredMethods())
            .filter(m -> m.getName().equals(methodName))
            .findFirst()
            .get();

        return new ParameterDescriptor(
            argumentPosition,
            method.getParameterTypes()[argumentPosition],
            method.getGenericParameterTypes()[argumentPosition],
            method);
    }

    public static abstract class TypeReference<T> {
        private final ParameterizedType type;

        protected TypeReference() {
            Type superClass = this.getClass().getGenericSuperclass();
            type = (ParameterizedType) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        }

        Class<?> rawType() {
            return (Class) type.getRawType();
        }
    }
}
