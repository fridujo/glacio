package com.github.fridujo.glacio.ls.tools;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileLogger {
    private final Path path;

    public FileLogger(String pathAsString) {
        path = Paths.get(pathAsString).toAbsolutePath();
    }

    public void log(String message) {
        try {
            Files.write(path, (message + "\n").getBytes(), APPEND, CREATE);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write log message to file [" + path + "]", e);
        }
    }
}
