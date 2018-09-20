package com.github.fridujo.glacio.running.runtime.io;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FileResourceIteratorFactoryTest {

    @Test
    void factory_for_anything() {
        FileResourceIteratorFactory fileResourceIteratorFactory = new FileResourceIteratorFactory();
        assertThat(fileResourceIteratorFactory.isFactoryFor(null)).isTrue();
    }
}
