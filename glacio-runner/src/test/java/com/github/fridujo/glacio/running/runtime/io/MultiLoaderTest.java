package com.github.fridujo.glacio.running.runtime.io;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MultiLoaderTest {

    @Test
    void load_files() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);

        Iterable<Resource> resources = resourceLoader.resources("src/test/resources/cucumber", ".properties");

        assertThat(resources).hasSize(3);
    }
}
