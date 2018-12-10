package com.github.fridujo.glacio.running.runtime.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.api.extension.Store;

public class ExtensionContextImpl implements ExtensionContext {
    private final Class<?> configurationClass;
    private final Map<String, Store> storesByNamespace = new ConcurrentHashMap<>();

    public ExtensionContextImpl(Class<?> configurationClass) {
        this.configurationClass = configurationClass;
    }

    @Override
    public Class<?> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    public Store getStore(String namespace) {
        return storesByNamespace.computeIfAbsent(namespace, StoreImpl::new);
    }
}
