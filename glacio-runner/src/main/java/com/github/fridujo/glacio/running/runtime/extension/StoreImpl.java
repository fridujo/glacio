package com.github.fridujo.glacio.running.runtime.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.github.fridujo.glacio.running.api.extension.Store;

public class StoreImpl implements Store {
    private final String namespace;
    private final Map<Object, Object> valuesByKey = new ConcurrentHashMap<>();

    public StoreImpl(String namespace) {
        this.namespace = namespace;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> V getOrComputeIfAbsent(K key, Function<K, V> defaultCreator, Class<V> requiredType) {
        Function<Object, Object> creator = o -> defaultCreator.apply((K) o);
        return (V) valuesByKey.computeIfAbsent(key, creator);
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public String toString() {
        return namespace + " Store";
    }
}
