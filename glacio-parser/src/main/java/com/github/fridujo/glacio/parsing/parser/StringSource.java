package com.github.fridujo.glacio.parsing.parser;

import java.io.UncheckedIOException;
import java.net.URI;

public interface StringSource {

    String getContent() throws UncheckedIOException;

    URI getURI();
}
