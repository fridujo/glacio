package com.github.fridujo.glacio.running.runtime.io;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

class FlatteningIteratorTest {

    @Test
    void flattens_iterators() {
        final FlatteningIterator<Integer> fi = new FlatteningIterator<>();
        fi.push(asList(3, 4).iterator());
        fi.push(asList(1, 2).iterator());

        assertThat(fi).toIterable().containsExactly(1, 2, 3, 4);

        assertThat(fi.hasNext()).isFalse();

        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> fi.next());
    }

    @Test
    void remove_operation_is_not_permitted() {
        FlatteningIterator<Integer> fi = new FlatteningIterator<>();

        assertThatExceptionOfType(UnsupportedOperationException.class)
            .isThrownBy(() -> fi.remove());
    }
}
