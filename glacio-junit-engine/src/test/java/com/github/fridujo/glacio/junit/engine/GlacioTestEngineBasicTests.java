package com.github.fridujo.glacio.junit.engine;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestEngine;

class GlacioTestEngineBasicTests {

    private final TestEngine glacio = new GlacioTestEngine();

    @Test
    void id() {
        assertThat(glacio.getId()).isEqualTo("glacio");
    }

    @Test
    void groupId() {
        assertThat(glacio.getGroupId()).hasValue("com.github.fridujo");
    }

    @Test
    void artifactId() {
        assertThat(glacio.getArtifactId()).hasValue("glacio-junit-engine");
    }

    @Test
    void version() {
        assertThat(glacio.getVersion()).hasValue("DEVELOPMENT");
    }
}
