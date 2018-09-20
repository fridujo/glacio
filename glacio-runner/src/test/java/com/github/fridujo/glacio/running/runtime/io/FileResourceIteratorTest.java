package com.github.fridujo.glacio.running.runtime.io;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;

import org.junit.jupiter.api.Test;

class FileResourceIteratorTest {

    @Test
    void remove_operation_is_not_permitted() {
        FileResourceIterator fileResourceIterator = FileResourceIterator.createClasspathFileResourceIterator(new File("src"), new File("src/test"), "test");

        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> fileResourceIterator.remove());
    }

    @Test
    void abstract_file_cannot_be_walked_through() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> FileResourceIterator.createClasspathFileResourceIterator(
                new AbstractFile("src"),
                new AbstractFile("src/test"),
                "test"))
            .withMessageStartingWith("Not a file or directory: ");
    }

    static class AbstractFile extends File {

        public AbstractFile(String pathname) {
            super(pathname);
        }

        public boolean isDirectory() {
            return false;
        }

        public boolean isFile() {
            return false;
        }
    }
}
