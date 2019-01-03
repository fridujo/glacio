package com.github.fridujo.glacio.running;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.configuration.GlacioConfiguration;
import com.github.fridujo.glacio.running.runtime.GlacioRunnerInitializationException;
import com.github.fridujo.glacio.running.runtime.RuntimeOptions;

class RuntimeOptionsParserTest {

    @Test
    void parse_empty() {
        List<String> argsList = Collections.emptyList();
        RuntimeOptionsParser runtimeOptionsParser = new RuntimeOptionsParser(argsList);

        RuntimeOptions runtimeOptions = runtimeOptionsParser.parse();

        assertThat(runtimeOptions.getFeaturePaths()).isEmpty();
        assertThat(runtimeOptions.getGluePaths()).isEmpty();
        assertThat(runtimeOptions.getConfigurationClasses()).isEmpty();
    }

    @Test
    void parse_featurepath_only() {
        List<String> argsList = Arrays.asList("/some/path", "some/relative/path");
        RuntimeOptionsParser runtimeOptionsParser = new RuntimeOptionsParser(argsList);

        RuntimeOptions runtimeOptions = runtimeOptionsParser.parse();

        assertThat(runtimeOptions.getFeaturePaths()).containsExactlyInAnyOrder("/some/path", "some/relative/path");
        assertThat(runtimeOptions.getGluePaths()).isEmpty();
        assertThat(runtimeOptions.getConfigurationClasses()).isEmpty();
    }

    @Test
    void parse_gluepath_only() {
        List<String> argsList = Arrays.asList("-g", "com.github", "--glue", "org.other");
        RuntimeOptionsParser runtimeOptionsParser = new RuntimeOptionsParser(argsList);

        RuntimeOptions runtimeOptions = runtimeOptionsParser.parse();

        assertThat(runtimeOptions.getFeaturePaths()).isEmpty();
        assertThat(runtimeOptions.getGluePaths()).containsExactlyInAnyOrder("com.github", "org.other");
        assertThat(runtimeOptions.getConfigurationClasses()).isEmpty();
    }

    @Test
    void parse_configuration_class_only() {
        List<String> argsList = Arrays.asList("-c",
            "com.github.fridujo.glacio.running.RuntimeOptionsParserTest$TestConfiguration",
            "--configuration-class", "com.github.fridujo.glacio.running.RuntimeOptionsParserTest$TestConfiguration");
        RuntimeOptionsParser runtimeOptionsParser = new RuntimeOptionsParser(argsList);

        RuntimeOptions runtimeOptions = runtimeOptionsParser.parse();

        assertThat(runtimeOptions.getFeaturePaths()).isEmpty();
        assertThat(runtimeOptions.getGluePaths()).isEmpty();
        assertThat(runtimeOptions.getConfigurationClasses()).hasSize(1).containsExactly(TestConfiguration.class);
    }

    @Test
    void parse_all() {
        List<String> argsList = Arrays.asList(
            "-g", "com.github",
            "/some/path",
            "--glue", "org.other",
            "-c", "com.github.fridujo.glacio.running.RuntimeOptionsParserTest$TestConfiguration",
            "--configuration-class", "com.github.fridujo.glacio.running.RuntimeOptionsParserTest$TestConfiguration"
        );
        RuntimeOptionsParser runtimeOptionsParser = new RuntimeOptionsParser(argsList);

        RuntimeOptions runtimeOptions = runtimeOptionsParser.parse();

        assertThat(runtimeOptions.getFeaturePaths()).containsExactly("/some/path");
        assertThat(runtimeOptions.getGluePaths()).containsExactlyInAnyOrder("com.github", "org.other");
        assertThat(runtimeOptions.getConfigurationClasses()).hasSize(1).containsExactly(TestConfiguration.class);
    }

    @Test
    void parsing_fails_when_configuration_class_is_absent() {
        List<String> argsList = Arrays.asList("-c", "test");
        RuntimeOptionsParser runtimeOptionsParser = new RuntimeOptionsParser(argsList);

        assertThatExceptionOfType(GlacioRunnerInitializationException.class)
            .isThrownBy(runtimeOptionsParser::parse)
            .withMessageContaining("Configuration[test] not found");
    }

    @GlacioConfiguration
    public static final class TestConfiguration {
    }
}
