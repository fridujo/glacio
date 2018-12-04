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
        Iterable<Resource> files = new ClasspathResourceLoader(Thread.currentThread().getContextClassLoader())
            .resources("META-INF/maven/org.assertj/assertj-core", ".properties");
        assertThat(files).isNotEmpty();
        Resource resource = files.iterator().next();
        assertThat(resource.getAbsolutePath()).endsWith(".jar!/META-INF/maven/org.assertj/assertj-core/pom.properties");
        assertThat(resource.getContent()).contains("groupId=org.assertj");
        assertThat(resource.getURI().toString()).endsWith("/pom.properties");
        assertThat(resource.getClassName("")).isEqualTo("META-INF.maven.org.assertj.assertj-core.pom.properties");
    }
}
