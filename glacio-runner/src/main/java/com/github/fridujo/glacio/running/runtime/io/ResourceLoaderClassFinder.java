package com.github.fridujo.glacio.running.runtime.io;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

public class ResourceLoaderClassFinder implements ClassFinder {
    private final ResourceLoader resourceLoader;
    private final ClassLoader classLoader;

    public ResourceLoaderClassFinder(ResourceLoader resourceLoader, ClassLoader classLoader) {
        this.resourceLoader = resourceLoader;
        this.classLoader = classLoader;
    }

    @Override
    public <T> Collection<Class<? extends T>> getDescendants(Class<T> parentType, String packageName) {
        Collection<Class<? extends T>> result = new HashSet<>();
        String packagePath = "classpath:" + packageName.replace('.', '/').replace(File.separatorChar, '/');
        for (Resource classResource : resourceLoader.resources(packagePath, ".class")) {
            String className = classResource.getClassName(".class");

            try {
                Class<?> clazz = loadClass(className);
                if (clazz != null && !parentType.equals(clazz) && parentType.isAssignableFrom(clazz)) {
                    result.add(clazz.asSubclass(parentType));
                }
            } catch (ClassNotFoundException ignore) {
            } catch (NoClassDefFoundError ignore) {
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> loadClass(String className) throws ClassNotFoundException {
        return (Class<? extends T>) classLoader.loadClass(className);
    }
}
