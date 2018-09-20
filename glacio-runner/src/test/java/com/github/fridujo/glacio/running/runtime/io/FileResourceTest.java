package com.github.fridujo.glacio.running.runtime.io;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

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
}
