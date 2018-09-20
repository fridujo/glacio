package com.github.fridujo.glacio.running.runtime.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

class FileResourceTest {

    @Test
    void for_classpath_files_get_path_should_return_relative_path_from_classpath_root() {
        // setup
        FileResource toTest1 = FileResource.createClasspathFileResource(new File("/testPath"), new File("/testPath/test1/test.feature"));
        FileResource toTest2 = FileResource.createClasspathFileResource(new File("testPath"), new File("testPath/test1/test.feature"));

        // test
        assertThat(toTest1.getPath())
            .isEqualTo("test1" + File.separator + "test.feature")
            .isEqualTo(toTest2.getPath());

        assertThat(toTest1.getAbsolutePath()).endsWith(File.separator + "testPath" + File.separator + "test1" + File.separator + "test.feature");
        assertThat(toTest2.getAbsolutePath()).endsWith(File.separator + "testPath" + File.separator + "test1" + File.separator + "test.feature");
    }

    @Test
    void for_classpath_files_get_content_should_return_the_file_content() {
        FileResource bar = FileResource.createClasspathFileResource(new File("src/test/resources"), new File("src/test/resources/cucumber/runtime/bar.properties"));
        assertThat(bar.getURI().toString()).endsWith("cucumber/runtime/bar.properties");
        assertThat(bar.getContent()).isEqualTo("bar=BAR\n");
    }

    @Test
    void for_filesystem_files_get_path_should_return_the_path() {
        // setup
        FileResource toTest1 = FileResource.createFileResource(new File("test1"), new File("test1/test.feature"));
        FileResource toTest2 = FileResource.createFileResource(new File("test1/test.feature"), new File("test1/test.feature"));

        // test
        assertThat(toTest1.getPath())
            .isEqualTo("test1" + File.separator + "test.feature")
            .isEqualTo(toTest2.getPath());
    }

    @Test
    void incoherent_root_should_throw() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() ->
                FileResource.createFileResource(
                    new File("test1"),
                    new File("test2"))
            ).withMessageContaining("is not a parent of");
    }

    @Test
    void for_absent_file_get_content_should_throw() {
        FileResource fileResource = FileResource.createFileResource(new File("test1"), new File("test1/test.feature"));

        assertThatExceptionOfType(UncheckedIOException.class)
            .isThrownBy(() -> fileResource.getContent());
    }

    @Test
    void get_class_name_remove_extension_and_replace_file_separators() {
        FileResource fileResource = FileResource.createClasspathFileResource(new File("/testPath"), new File("/testPath/test1/test.feature"));

        assertThat(fileResource.getClassName(".feature")).isEqualTo("test1.test");
    }
}
