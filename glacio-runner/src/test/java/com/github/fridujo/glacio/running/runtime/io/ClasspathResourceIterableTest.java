package com.github.fridujo.glacio.running.runtime.io;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.junit.jupiter.api.Test;

class ClasspathResourceIterableTest {

    @Test
    void class_loader_get_resource_exception_is_propagated() {
        ClassLoader classLoader = new TestClassLoader();
        ClasspathResourceIterable resources = new ClasspathResourceIterable(classLoader, "$not existing", "test");

        assertThatExceptionOfType(GlacioIOException.class)
            .isThrownBy(() -> resources.iterator())
            .withCauseExactlyInstanceOf(IOException.class);
    }

    static class TestClassLoader extends ClassLoader {
        public Enumeration<URL> getResources(String name) throws IOException {
            throw new IOException("test");
        }
    }
}
