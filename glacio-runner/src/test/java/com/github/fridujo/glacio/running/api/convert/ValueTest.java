package com.github.fridujo.glacio.running.api.convert;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ValueTest {

    @Test
    void toString_displays_value_when_present() {
        assertThat(Value.present(null)).hasToString("null");
    }

    @Test
    void toString_displays_absent_when_absent() {
        assertThat(Value.absent()).hasToString("<absent>");
    }
}
