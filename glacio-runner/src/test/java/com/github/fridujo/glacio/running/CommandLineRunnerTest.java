package com.github.fridujo.glacio.running;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.runtime.glue.MissingStepImplementationException;

class CommandLineRunnerTest {

    @Test
    void nominal() {
        CommandLineRunner.main(new String[]{
            "-g", "com.github.fridujo.glacio.sample",
            "classpath:valid_features"
        });

        assertThat(true).as("no exception have been thrown").isTrue();
    }

    @Test
    void invalid_scenarios() {
        assertThatExceptionOfType(MissingStepImplementationException.class)
            .isThrownBy(() ->
                CommandLineRunner.main(new String[]{
                    "-g", "com.github.fridujo.glacio.sample",
                    "classpath:invalid_features/invalid_scenarios.feature"
                }))
            .withMessageContaining("No matching step def found for step: Given a step with no matching method");
    }
}
