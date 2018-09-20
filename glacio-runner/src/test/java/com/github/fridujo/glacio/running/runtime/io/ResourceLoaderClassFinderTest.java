package com.github.fridujo.glacio.running.runtime.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.Test;

class ResourceLoaderClassFinderTest {

    @Test
    void find_descendants_of_Number() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ResourceLoaderClassFinder resourceLoaderClassFinder = new ResourceLoaderClassFinder(new MultiLoader(classLoader), classLoader);

        Collection<Class<? extends ClassFinder>> descendants = resourceLoaderClassFinder.getDescendants(ClassFinder.class, "com.github.fridujo.glacio.running.runtime");

        assertThat(descendants).containsExactlyInAnyOrder(ResourceLoaderClassFinder.class);
    }
}
