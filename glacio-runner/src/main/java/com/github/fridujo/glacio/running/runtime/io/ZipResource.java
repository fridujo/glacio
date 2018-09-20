package com.github.fridujo.glacio.running.runtime.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResource implements Resource {
    private final ZipFile jarFile;
    private final ZipEntry jarEntry;
    private final URI uri;

    public ZipResource(ZipFile jarFile, ZipEntry jarEntry, URI uri) {
        this.jarFile = jarFile;
        this.jarEntry = jarEntry;
        this.uri = uri;
    }

    @Override
    public String getPath() {
        return jarEntry.getName();
    }

    @Override
    public String getAbsolutePath() {
        return jarFile.getName() + "!/" + getPath();
    }

    @Override
    public String getContent() throws UncheckedIOException {
        try (InputStream is = jarFile.getInputStream(jarEntry);
             Scanner s = new Scanner(is).useDelimiter("\\A")) {
            return s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public URI getURI() {
        return uri;
    }

    @Override
    public String getClassName(String extension) {
        String path = getPath();
        return path.substring(0, path.length() - extension.length()).replace('/', '.');
    }
}
