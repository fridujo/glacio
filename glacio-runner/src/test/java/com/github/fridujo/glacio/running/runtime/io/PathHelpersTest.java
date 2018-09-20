package com.github.fridujo.glacio.running.runtime.io;

import static com.github.fridujo.glacio.running.runtime.io.PathHelpers.filePath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Test;

class PathHelpersTest {

    @Test
    void computes_file_path_for_file_url() throws MalformedURLException {
        URL url = new URL("file:/Users/First%20Last/.m2/repository/info/cukes/cucumber-java/1.2.2/cucumber-java-1.2.2.jar");
        File fileFromFilePath = new File(filePath(url));
        File expectedFile = new File("/Users/First Last/.m2/repository/info/cukes/cucumber-java/1.2.2/cucumber-java-1.2.2.jar");
        assertThat(fileFromFilePath).isEqualTo(expectedFile);
    }

    @Test
    void filePath_throws_when_protocol_is_not_file() {
        assertThatExceptionOfType(GlacioIOException.class)
            .isThrownBy(() -> filePath(new URL("https://host")))
            .withMessage("Expected a file URL: https://host");
    }
}
