package com.github.fridujo.glacio.ls.storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

public class LruCacheWithFsBackup<K, V> extends LinkedHashMap<K, V> {
    private final int maxEntries;
    private final Class<V> valueClass;
    private final Set<Object> fsStoredKeys = new HashSet<>();
    private final CloseablePath storageFolder = new CloseablePath(createTempDirectory());
    private final Gson gson = new Gson();

    public LruCacheWithFsBackup(int maxEntries, Class<V> valueClass) {
        this.maxEntries = maxEntries;
        this.valueClass = valueClass;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> storageFolder.close()));
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        storeToFS(eldest);

        return size() > maxEntries;
    }

    public V get(Object key) {
        V v = super.get(key);
        if (v == null && fsStoredKeys.contains(key)) {
            return getFromFS(key);
        } else {
            return v;
        }
    }

    public V remove(Object key) {
        V v = super.remove(key);
        if (v == null && fsStoredKeys.contains(key)) {
            return deleteFromFS(key);
        } else {
            return v;
        }
    }

    private void storeToFS(Map.Entry<K, V> eldest) {
        Path cachedFilePath = cachedFilePath(eldest.getKey());
        try {
            Files.write(cachedFilePath, gson.toJson(eldest.getValue()).getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        fsStoredKeys.add(eldest.getKey());
    }

    private V getFromFS(Object key) {
        Path cachedFilePath = cachedFilePath(key);
        V cachedValue = readNoCacheFromFs(cachedFilePath);
        put((K) key, cachedValue);
        fsStoredKeys.remove(key);
        return cachedValue;
    }

    private V deleteFromFS(Object key) {
        Path cachedFilePath = cachedFilePath(key);
        V cachedValue = readNoCacheFromFs(cachedFilePath);
        try {
            Files.delete(cachedFilePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        fsStoredKeys.remove(key);
        return cachedValue;
    }

    private V readNoCacheFromFs(Path cachedFilePath) {
        V cachedValue;
        try {
            cachedValue = gson.fromJson(new String(Files.readAllBytes(cachedFilePath)), valueClass);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return cachedValue;
    }

    private Path cachedFilePath(Object key) {
        return storageFolder.get().resolve(String.valueOf(key.hashCode()));
    }

    private Path createTempDirectory() {
        try {
            return Files.createTempDirectory("glacio-ls-files");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
