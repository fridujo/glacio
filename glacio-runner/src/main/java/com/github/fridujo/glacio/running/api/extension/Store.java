package com.github.fridujo.glacio.running.api.extension;

import java.util.function.Function;

public interface Store {

    <K, V> V getOrComputeIfAbsent(K key, Function<K, V> defaultCreator, Class<V> requiredType);

    Object remove(Object key);
}
