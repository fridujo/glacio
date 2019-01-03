package com.github.fridujo.glacio.running.runtime.glue;

import static java.util.Arrays.stream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.fridujo.glacio.running.GlacioRunnerException;

public class GlueFactory {

    private final Map<Class<?>, Object> scenarioScopedInstances = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getGlue(Class<T> glueClass) {
        if (!scenarioScopedInstances.containsKey(glueClass)) {
            scenarioScopedInstances.put(glueClass, createInstance(glueClass));
        }
        return (T) scenarioScopedInstances.get(glueClass);
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(Class<T> glueClass) {
        Constructor<T>[] declaredConstructors = (Constructor<T>[]) glueClass.getDeclaredConstructors();
        final Constructor<T> constructor;
        if (declaredConstructors.length > 1) {
            Optional<Constructor<T>> defaultConstructor = stream(declaredConstructors)
                .filter(c -> c.getParameterCount() == 0)
                .findAny();
            if (!defaultConstructor.isPresent()) {
                throw new GlacioRunnerException(instantiationError(glueClass, "must have only one constructor, or a default one"));
            } else {
                constructor = defaultConstructor.get();
            }
        } else {
            constructor = declaredConstructors[0];
        }

        int modifiers = constructor.getModifiers();
        if (Modifier.isPrivate(modifiers)) {
            throw new GlacioRunnerException(instantiationError(glueClass, "constructor is private"));
        }

        if (!Modifier.isPublic(modifiers)) {
            constructor.setAccessible(true);
        }
        try {
            return constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new GlacioRunnerException(instantiationError(glueClass, e.getMessage()), e);
        }
    }

    private String instantiationError(Class<?> clazz, String message) {
        return "Cannot create instance of class " + clazz.getSimpleName() + ": " + message;
    }

    public void reset() {
        scenarioScopedInstances.clear();
    }
}
