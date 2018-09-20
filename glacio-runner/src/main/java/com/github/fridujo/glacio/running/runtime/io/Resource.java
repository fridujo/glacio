package com.github.fridujo.glacio.running.runtime.io;

import com.github.fridujo.glacio.parsing.model.StringSource;

public interface Resource extends StringSource {
    String getPath();

    String getAbsolutePath();

    String getClassName(String extension);
}
