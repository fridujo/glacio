package com.github.fridujo.glacio.running.runtime.event;

public interface EventAware {

    void beforeInitialization();

    void beforeAllFeatures();

    void beforeFeature();

    void beforeExample();

    void afterAllFeatures();

    void afterFeature();

    void afterExample();
}
