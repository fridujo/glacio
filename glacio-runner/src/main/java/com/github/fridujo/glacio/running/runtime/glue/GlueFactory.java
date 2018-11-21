package com.github.fridujo.glacio.running.runtime.glue;

import java.util.HashMap;
import java.util.Map;

import com.github.fridujo.glacio.running.runtime.BeforeExampleEventAware;

// TODO plugin mechanism for constructor injection (similar to JUnit5)
// TODO system property check to enable ServiceLoaded plugins
public class GlueFactory implements BeforeExampleEventAware {

    private final Map<Class<?>, Object> scenarioScopedInstances = new HashMap<>();

    public Object getGlue(Class<?> glueClass) {
        if (!scenarioScopedInstances.containsKey(glueClass)) {
            try {
                scenarioScopedInstances.put(glueClass, glueClass.getDeclaredConstructor().newInstance());
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot instantiate stepdef class " + glueClass.getName(), e);
            }
        }
        return scenarioScopedInstances.get(glueClass);
    }

    public void beforeExample() {
        scenarioScopedInstances.clear();
    }
}
