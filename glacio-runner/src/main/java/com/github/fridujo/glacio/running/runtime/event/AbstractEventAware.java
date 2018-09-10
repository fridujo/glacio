package com.github.fridujo.glacio.running.runtime.event;

public abstract class AbstractEventAware implements EventAware {
    @Override
    public void beforeInitialization() {
        // To override
    }

    @Override
    public void beforeAllFeatures() {
        // To override
    }

    @Override
    public void beforeFeature() {
        // To override
    }

    @Override
    public void beforeExample() {
        // To override
    }

    @Override
    public void afterAllFeatures() {
        // To override
    }

    @Override
    public void afterFeature() {
        // To override
    }

    @Override
    public void afterExample() {
        // To override
    }
}
