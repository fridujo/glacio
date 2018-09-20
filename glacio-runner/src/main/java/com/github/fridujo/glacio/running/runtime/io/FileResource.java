package com.github.fridujo.glacio.running.runtime.io;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;

public class FileResource implements Resource {
    private final File root;
    private final File file;
    private final boolean classpathFileResource;

    private FileResource(File root, File file, boolean classpathFileResource) {
        this.root = root;
        this.file = file;
        this.classpathFileResource = classpathFileResource;
        if (!file.getAbsolutePath().startsWith(root.getAbsolutePath())) {
            throw new IllegalArgumentException(file.getAbsolutePath() + " is not a parent of " + root.getAbsolutePath());
        }
    }

    public static FileResource createFileResource(File root, File file) {
        return new FileResource(root, file, false);
    }

    public static FileResource createClasspathFileResource(File root, File file) {
        return new FileResource(root, file, true);
    }

    @Override
    public String getPath() {
        if (classpathFileResource) {
            return file.getAbsolutePath().substring(root.getAbsolutePath().length() + 1);
        } else {
            return file.getPath();
        }
    }

    @Override
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    @Override
    public String getContent() throws UncheckedIOException {
        try {
            return new String(Files.readAllBytes(file.toPath().toAbsolutePath()));
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
}
