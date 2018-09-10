package com.github.fridujo.glacio.running.runtime.glue;

import com.github.fridujo.glacio.running.runtime.event.AbstractEventAware;

import java.util.HashMap;
import java.util.Map;

public class SimpleGlueFactory extends AbstractEventAware implements GlueFactory {
    private final Map<Class<?>, Object> scenarioScopedInstances = new HashMap<>();

    @Override
    public Object getGlue(Class<?> glueClass) {
        if (!scenarioScopedInstances.containsKey(glueClass)) {
            try {
                scenarioScopedInstances.put(glueClass, glueClass.getDeclaredConstructor().newInstance());
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot instantiate stepdef class " + glueClass.getName());
            }
        }
        return scenarioScopedInstances.get(glueClass);
    }

    @Override
    public void beforeExample() {
        scenarioScopedInstances.clear();
    }
}
