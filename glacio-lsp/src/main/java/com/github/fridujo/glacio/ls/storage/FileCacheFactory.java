package com.github.fridujo.glacio.ls.storage;

public class FileCacheFactory {

    public static FileCache inMemoryFileCache() {
        return new InMemoryFileCache();
    }
}
