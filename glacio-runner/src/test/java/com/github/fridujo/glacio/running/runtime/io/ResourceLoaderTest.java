package com.github.fridujo.glacio.running.runtime.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.jupiter.api.Test;

class ResourceLoaderTest {
    private final File dir;

    ResourceLoaderTest() throws UnsupportedEncodingException {
        dir = new File(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8"));
    }

    @Test
    void loads_resources_from_filesystem_dir() {
        Iterable<Resource> files = new FileResourceLoader().resources(dir.getAbsolutePath(), ".properties");
        assertThat(files).hasSize(3);
    }

    @Test
    void loads_resource_from_filesystem_file() {
        File file = new File(dir, "cucumber/runtime/bar.properties");
        Iterable<Resource> files = new FileResourceLoader().resources(file.getPath(), ".doesntmatter");
        assertThat(files).hasSize(1);
    }

    @Test
    void loads_resources_from_jar_on_classpath() {
        Iterable<Resource> files = new ClasspathResourceLoader(Thread.currentThread().getContextClassLoader()).resources("META-INF/services", ".TestEngine");
        assertThat(files).hasSize(1);
        Resource resource = files.iterator().next();
        assertThat(resource.getAbsolutePath()).endsWith(".jar!/META-INF/services/org.junit.platform.engine.TestEngine");
        assertThat(resource.getContent()).contains("org.junit.jupiter.engine.JupiterTestEngine");
        assertThat(resource.getURI().toString()).endsWith("/org.junit.platform.engine.TestEngine");
        assertThat(resource.getClassName("")).isEqualTo("META-INF.services.org.junit.platform.engine.TestEngine");
    }
}
