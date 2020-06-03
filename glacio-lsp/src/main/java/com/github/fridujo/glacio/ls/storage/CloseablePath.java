package com.github.fridujo.glacio.ls.storage;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.util.stream.Collectors.joining;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

class CloseablePath implements Closeable {

    private final Path dir;

    CloseablePath(Path dir) {
        this.dir = dir;
    }

    Path get() {
        return dir;
    }

    @Override
    public void close() {
        SortedMap<Path, IOException> failures = deleteAllFilesAndDirectories();
        if (!failures.isEmpty()) {
            throw createIOExceptionWithAttachedFailures(failures);
        }
    }

    private SortedMap<Path, IOException> deleteAllFilesAndDirectories() {
        if (Files.notExists(dir)) {
            return Collections.emptySortedMap();
        }

        SortedMap<Path, IOException> failures = new TreeMap<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    return deleteAndContinue(file);
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return deleteAndContinue(dir);
                }

                private FileVisitResult deleteAndContinue(Path path) {
                    try {
                        Files.delete(path);
                    } catch (NoSuchFileException ignore) {
                        // ignore
                    } catch (DirectoryNotEmptyException exception) {
                        failures.put(path, exception);
                    } catch (IOException exception) {
                        makeWritableAndTryToDeleteAgain(path, exception);
                    }
                    return CONTINUE;
                }

                private void makeWritableAndTryToDeleteAgain(Path path, IOException exception) {
                    try {
                        makeWritable(path);
                        Files.delete(path);
                    } catch (Exception suppressed) {
                        exception.addSuppressed(suppressed);
                        failures.put(path, exception);
                    }
                }

                private void makeWritable(Path path) {
                    boolean writable = path.toFile().setWritable(true);
                    if (!writable) {
                        throw new RuntimeException("Attempt to make file '" + path + "' writable failed");
                    }
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return failures;
    }

    private UncheckedIOException createIOExceptionWithAttachedFailures(SortedMap<Path, IOException> failures) {
        // @formatter:off
            String joinedPaths = failures.keySet().stream()
                .peek(this::tryToDeleteOnExit)
                .map(this::relativizeSafely)
                .map(String::valueOf)
                .collect(joining(", "));
            // @formatter:on
        IOException exception = new IOException("Failed to delete temp directory " + dir.toAbsolutePath()
            + ". The following paths could not be deleted (see suppressed exceptions for details): "
            + joinedPaths);
        failures.values().forEach(exception::addSuppressed);
        return new UncheckedIOException(exception);
    }

    private void tryToDeleteOnExit(Path path) {
        try {
            path.toFile().deleteOnExit();
        } catch (UnsupportedOperationException ignore) {
        }
    }

    private Path relativizeSafely(Path path) {
        try {
            return dir.relativize(path);
        } catch (IllegalArgumentException e) {
            return path;
        }
    }
}
