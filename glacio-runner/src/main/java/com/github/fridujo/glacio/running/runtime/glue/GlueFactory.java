package com.github.fridujo.glacio.running.runtime.glue;

import static java.util.Arrays.stream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.github.fridujo.glacio.running.GlacioRunnerException;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.api.extension.ParameterContext;
import com.github.fridujo.glacio.running.api.extension.ParameterResolver;
import com.github.fridujo.glacio.running.runtime.extension.ParameterContextImpl;

public class GlueFactory {

    private final Map<Class<?>, Object> scenarioScopedInstances = new HashMap<>();
    private final Set<ParameterResolver> parameterResolvers;
    private final ExtensionContext extensionContext;

    public GlueFactory(Set<ParameterResolver> parameterResolvers, ExtensionContext extensionContext) {
        this.parameterResolvers = parameterResolvers;
        this.extensionContext = extensionContext;
    }

    public <T> T getGlue(Class<T> glueClass) {
        if (!scenarioScopedInstances.containsKey(glueClass)) {
            scenarioScopedInstances.put(glueClass, createInstance(glueClass));
        }
        return (T) scenarioScopedInstances.get(glueClass);
    }

    private <T> T createInstance(Class<T> glueClass) {
        final Constructor<T> constructor = findConstructor(glueClass);

        List<ParameterContext> parameterContexts = buildParametersContext(constructor);
        Object[] parameters = parameterContexts.stream().map(this::toParameter).toArray();

        try {
            return constructor.newInstance(parameters);
        } catch (ReflectiveOperationException e) {
            throw new GlacioRunnerException(instantiationError(glueClass, e.getMessage()), e);
        }
    }

    private <T> Constructor<T> findConstructor(Class<T> glueClass) {
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

        checkAndSetVisibility(glueClass, constructor);
        return constructor;
    }

    private <T> void checkAndSetVisibility(Class<T> glueClass, Constructor<T> constructor) {
        int modifiers = constructor.getModifiers();
        if (Modifier.isPrivate(modifiers)) {
            throw new GlacioRunnerException(instantiationError(glueClass, "constructor is private"));
        }
        if (!Modifier.isPublic(modifiers)) {
            constructor.setAccessible(true);
        }
    }

    private String instantiationError(Class<?> clazz, String message) {
        return "Cannot create instance of class " + clazz.getSimpleName() + ": " + message;
    }

    private Object toParameter(ParameterContext parameterContext) {
        for (ParameterResolver parameterResolver : parameterResolvers) {
            if (parameterResolver.supportsParameter(parameterContext, extensionContext)) {
                return parameterResolver.resolveParameter(parameterContext, extensionContext);
            }
        }
        throw new GlacioRunnerException(
            instantiationError(parameterContext.getDeclaringExecutable().getDeclaringClass(),
                "No " + ParameterResolver.class.getSimpleName() + " matches parameter " + parameterContext));
    }

    private List<ParameterContext> buildParametersContext(Constructor<?> constructor) {
        List<ParameterContext> parameterContexts = new ArrayList<>();
        for (int index = 0; index < constructor.getParameterCount(); index++) {
            parameterContexts.add(new ParameterContextImpl(constructor, constructor.getParameters()[index], index));
        }
        return parameterContexts;
    }

    public void reset() {
        scenarioScopedInstances.clear();
    }
}
