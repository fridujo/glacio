package com.github.fridujo.glacio.running.runtime.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.stream.Stream;

import static com.github.fridujo.glacio.running.runtime.io.Helpers.filePath;
import static com.github.fridujo.glacio.running.runtime.io.Helpers.jarFilePath;
import static org.assertj.core.api.Assertions.assertThat;

class HelpersTest {
    private static final URLStreamHandler NULL_URL_STREAM_HANDLER = new URLStreamHandler() {
        @Override
        protected URLConnection openConnection(URL u) {
            throw new UnsupportedOperationException();
        }
    };

    static Stream<String> jarFilePaths() throws MalformedURLException {
        return Stream.of(
            jarFilePath(new URL("jar:file:foo%20bar+zap/cucumber-core.jar!/cucumber/runtime/io")),
            jarFilePath(new URL(null, "zip:file:foo%20bar+zap/cucumber-core.jar!/cucumber/runtime/io", NULL_URL_STREAM_HANDLER)),
            jarFilePath(new URL(null, "wsjar:file:foo%20bar+zap/cucumber-core.jar!/cucumber/runtime/io", NULL_URL_STREAM_HANDLER)),
            jarFilePath(new URL("jar:file:foo%20bar+zap/cucumber-core.jar!/")),
            jarFilePath(new URL(null, "zip:file:foo%20bar+zap/cucumber-core.jar!/", NULL_URL_STREAM_HANDLER)),
            jarFilePath(new URL(null, "wsjar:file:foo%20bar+zap/cucumber-core.jar!/", NULL_URL_STREAM_HANDLER))
        );
    }

    @Test
    void computes_file_path_for_file_url() throws MalformedURLException {
        URL url = new URL("file:/Users/First%20Last/.m2/repository/info/cukes/cucumber-java/1.2.2/cucumber-java-1.2.2.jar");
        File fileFromFilePath = new File(filePath(url));
        File expectedFile = new File("/Users/First Last/.m2/repository/info/cukes/cucumber-java/1.2.2/cucumber-java-1.2.2.jar");
        assertThat(fileFromFilePath).isEqualTo(expectedFile);
    }

    @ParameterizedTest
    @MethodSource("jarFilePaths")
    void computes_file_path_for_jar_protocols(String jarFilePath) {
        assertThat(jarFilePath).isEqualTo("foo bar+zap/cucumber-core.jar");
    }
}
