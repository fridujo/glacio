package com.github.fridujo.glacio.running.runtime.io;

public interface ResourceLoader {
    Iterable<Resource> resources(String path, String suffix);
}
