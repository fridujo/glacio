package com.github.fridujo.glacio.running.runtime.io;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;

public class FileResource implements Resource {
    private final File root;
    private final File file;

    public FileResource(File root, File file) {
        this.root = root;
        this.file = file;
        if (!file.getAbsolutePath().startsWith(root.getAbsolutePath())) {
            throw new IllegalArgumentException(file.getAbsolutePath() + " is not a parent of " + root.getAbsolutePath());
        }
    }

    @Override
    public String getPath() {
        if (file.equals(root)) {
            return file.getPath();
        } else {
            return file.getAbsolutePath().substring(root.getAbsolutePath().length() + 1);
        }
    }

    @Override
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    @Override
    public String getContent() throws UncheckedIOException {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public URI getURI() {
        return file.toURI();
    }

    @Override
    public String getClassName(String extension) {
        String path = getPath();
        return path.substring(0, path.length() - extension.length()).replace(File.separatorChar, '.');
    }

    public File getFile() {
        return file;
    }
}
