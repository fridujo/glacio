package com.github.fridujo.glacio.running.api.extension;

public interface ExtensionContext {

    Class<?> getConfigurationClass();

    Store getStore(String namespace);
}
