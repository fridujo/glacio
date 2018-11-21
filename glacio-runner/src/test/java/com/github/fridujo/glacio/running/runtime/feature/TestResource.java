package com.github.fridujo.glacio.running.runtime.feature;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;

import com.github.fridujo.glacio.running.runtime.io.Resource;

public class TestResource implements Resource {

    private final String path;
    private final String absolutePath;
    private final String className;
    private final String content;
    private final URI uri;
    private final UncheckedIOException ex;

    public TestResource(String path, String absolutePath, String className, String content, URI uri, UncheckedIOException ex) {
        this.path = path;
        this.absolutePath = absolutePath;
        this.className = className;
        this.content = content;
        this.uri = uri;
        this.ex = ex;
    }

    public static TestResource build() {
        return new TestResource(null, null, null, null, null, null);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public String getClassName(String extension) {
        return className;
    }

    @Override
    public String getContent() throws UncheckedIOException {
        if (ex != null) {
            throw ex;
        }
        return content;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    public TestResource withContent(String content) {
        return new TestResource(path, absolutePath, className, content, uri, ex);
    }

    public TestResource withAbsolutePath(String absolutePath) {
        return new TestResource(path, absolutePath, className, content, uri, ex);
    }

    public TestResource throwing() {
        UncheckedIOException ex = new UncheckedIOException("test", new IOException());
        return new TestResource(path, absolutePath, className, content, uri, ex);
    }
}
