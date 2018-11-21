package com.github.fridujo.glacio.running;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.runtime.RuntimeOptions;

class RuntimeOptionsParserTest {

    @Test
    void parse_empty() {
        List<String> argsList = Collections.emptyList();
        RuntimeOptionsParser runtimeOptionsParser = new RuntimeOptionsParser(argsList);

        RuntimeOptions runtimeOptions = runtimeOptionsParser.parse();

        assertThat(runtimeOptions.getFeaturePaths()).isEmpty();
        assertThat(runtimeOptions.getGluePaths()).isEmpty();
    }

    @Test
    void parse_featurepath_only() {
        List<String> argsList = Arrays.asList("/some/path", "some/relative/path");
        RuntimeOptionsParser runtimeOptionsParser = new RuntimeOptionsParser(argsList);

        RuntimeOptions runtimeOptions = runtimeOptionsParser.parse();

        assertThat(runtimeOptions.getFeaturePaths()).containsExactlyInAnyOrder("/some/path", "some/relative/path");
        assertThat(runtimeOptions.getGluePaths()).isEmpty();
    }

    @Test
    void parse_gluepath_only() {
        List<String> argsList = Arrays.asList("-g", "com.github", "-g", "org.other");
        RuntimeOptionsParser runtimeOptionsParser = new RuntimeOptionsParser(argsList);

        RuntimeOptions runtimeOptions = runtimeOptionsParser.parse();

        assertThat(runtimeOptions.getFeaturePaths()).isEmpty();
        assertThat(runtimeOptions.getGluePaths()).containsExactlyInAnyOrder("com.github", "org.other");
    }

    @Test
    void parse_all() {
        List<String> argsList = Arrays.asList("-g", "com.github", "/some/path", "-g", "org.other");
        RuntimeOptionsParser runtimeOptionsParser = new RuntimeOptionsParser(argsList);

        RuntimeOptions runtimeOptions = runtimeOptionsParser.parse();

        assertThat(runtimeOptions.getFeaturePaths()).containsExactly("/some/path");
        assertThat(runtimeOptions.getGluePaths()).containsExactlyInAnyOrder("com.github", "org.other");
    }
}
