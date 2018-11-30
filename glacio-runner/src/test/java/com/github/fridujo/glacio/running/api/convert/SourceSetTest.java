package com.github.fridujo.glacio.running.api.convert;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SourceSetTest {

    @Test
    void valid_position_returns_matching_value() {
        SourceSet test = SourceSet.fromRaw("test");

        assertThat(test.atPosition(0).value).isEqualTo("test");
    }

    @Test
    void valid_position_returns_absent() {
        SourceSet test = SourceSet.fromRaw(new Object[]{null});

        assertThat(test.atPosition(1).present).isFalse();
    }
}
