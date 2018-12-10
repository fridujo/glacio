package com.github.fridujo.glacio.running.runtime.glue;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.fridujo.glacio.running.GlacioRunnerException;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.api.extension.ParameterContext;
import com.github.fridujo.glacio.running.api.extension.ParameterResolver;
import com.github.fridujo.glacio.running.runtime.BeforeExampleEventAware;

public class GlueFactory implements BeforeExampleEventAware {

    private final Map<Class<?>, Object> scenarioScopedInstances = new HashMap<>();
    private final Set<ParameterResolver> parameterResolvers;
    private final ExtensionContext extensionContext;

    public GlueFactory(Set<ParameterResolver> parameterResolvers, ExtensionContext extensionContext) {
        this.parameterResolvers = parameterResolvers;
        this.extensionContext = extensionContext;
    }

    public Object getGlue(Class<?> glueClass) {
        if (!scenarioScopedInstances.containsKey(glueClass)) {
            scenarioScopedInstances.put(glueClass, createInstance(glueClass));
        }
        return scenarioScopedInstances.get(glueClass);
    }

    private Object createInstance(Class<?> glueClass) {
        Constructor<?>[] declaredConstructors = glueClass.getDeclaredConstructors();
        if (declaredConstructors.length != 1) {
            throw new GlacioRunnerException("Glue class" + glueClass.getName() + " must have one constructor");
        }
        Constructor<?> constructor = declaredConstructors[0];
        List<ParameterContext> parameterContexts = buildParametersContext(constructor);
        Object[] parameters = parameterContexts.stream().map(this::toParameter).toArray();

        try {
            return constructor.newInstance(parameters);
        } catch (ReflectiveOperationException e) {
            throw new GlacioRunnerException("Cannot create instance of " + glueClass, e);
        }
    }

    private Object toParameter(ParameterContext parameterContext) {
        for (ParameterResolver parameterResolver : parameterResolvers) {
            if (parameterResolver.supportsParameter(parameterContext, extensionContext)) {
                return parameterResolver.resolveParameter(parameterContext, extensionContext);
            }
        }
        throw new GlacioRunnerException("No " + ParameterResolver.class.getSimpleName() +
            " matches parameter " + parameterContext);
    }

    private List<ParameterContext> buildParametersContext(Constructor<?> constructor) {
        List<ParameterContext> parameterContexts = new ArrayList<>();
        for (int index = 0; index < constructor.getParameterCount(); index++) {
            parameterContexts.add(new ParameterContextImpl(constructor, constructor.getParameters()[index], index));
        }
        return parameterContexts;
    }

    public void beforeExample() {
        scenarioScopedInstances.clear();
    }
}
