package com.github.fridujo.glacio.running.runtime.io;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

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

    @ParameterizedTest
    @MethodSource("url_protocols")
    void is_factory_for_jar_protocols(URL urlToCheck, boolean isFactoryCompliant) {
        ZipResourceIteratorFactory factory = new ZipResourceIteratorFactory();

        assertThat(factory.isFactoryFor(urlToCheck)).isEqualTo(isFactoryCompliant);
    }
}
