package com.github.fridujo.glacio.running.runtime.io;

import static com.github.fridujo.glacio.running.runtime.io.ZipResourceIteratorFactory.jarFilePath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.of;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

// https://github.com/cucumber/cucumber-jvm/issues/808
class ZipResourceIteratorFactoryTest {

    private static final URLStreamHandler NULL_URL_STREAM_HANDLER = new URLStreamHandler() {
        @Override
        protected URLConnection openConnection(URL u) {
            throw new UnsupportedOperationException();
        }
    };

    static Stream<Arguments> url_protocols() throws MalformedURLException {
        return Stream.of(
            of(new URL("jar:file:cucumber-core.jar!/cucumber/runtime/io"), true),
            of(new URL(null, "zip:file:cucumber-core.jar!/cucumber/runtime/io", NULL_URL_STREAM_HANDLER), true),
            of(new URL(null, "wsjar:file:cucumber-core.jar!/cucumber/runtime/io", NULL_URL_STREAM_HANDLER), true),
            of(new URL("file:cucumber-core"), false),
            of(new URL("http://http://cukes.info/cucumber-core.jar"), false)
        );
    }

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

    @ParameterizedTest
    @MethodSource("url_protocols")
    void is_factory_for_jar_protocols(URL urlToCheck, boolean isFactoryCompliant) {
        ZipResourceIteratorFactory factory = new ZipResourceIteratorFactory();

        assertThat(factory.isFactoryFor(urlToCheck)).isEqualTo(isFactoryCompliant);
    }

    @ParameterizedTest
    @MethodSource("jarFilePaths")
    void computes_file_path_for_jar_protocols(String jarFilePath) {
        assertThat(jarFilePath).isEqualTo("foo bar+zap/cucumber-core.jar");
    }

    @Test
    void createIterator_throws_when_jar_is_absent() throws MalformedURLException {
        URL url = new URL("jar:file:unexisting.jar!/test");
        assertThatExceptionOfType(GlacioIOException.class)
            .isThrownBy(() -> new ZipResourceIteratorFactory().createIterator(url, "test", ".properties"))
        ;
    }
}
