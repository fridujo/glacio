package com.github.fridujo.glacio.running.runtime.io;

import com.github.fridujo.glacio.parsing.parser.StringSource;

import java.io.UncheckedIOException;

public interface Resource extends StringSource {
    String getPath();

    String getAbsolutePath();

    String getContent() throws UncheckedIOException;

    String getClassName(String extension);
}
