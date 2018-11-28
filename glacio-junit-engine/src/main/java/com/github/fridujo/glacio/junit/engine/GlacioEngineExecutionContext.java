package com.github.fridujo.glacio.junit.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.junit.platform.engine.support.hierarchical.Node;

import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.BeforeExampleEventAware;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;

public class GlacioEngineExecutionContext implements EngineExecutionContext {

    private final ExecutableLookup executableLookup;
    private final BeforeExampleEventAware beforeExampleEventAware;
    private final Map<UniqueId, Step> firstFailedStepByExampleId = new ConcurrentHashMap<>();

    public GlacioEngineExecutionContext(ExecutableLookup executableLookup, BeforeExampleEventAware beforeExampleEventAware) {
        this.executableLookup = executableLookup;
        this.beforeExampleEventAware = beforeExampleEventAware;
    }

    public ExecutableLookup getExecutableLookup() {
        return executableLookup;
    }

    public BeforeExampleEventAware getBeforeExampleEventAware() {
        return beforeExampleEventAware;
    }

    public void registerFailure(UniqueId exampleId, Step step) {
        firstFailedStepByExampleId.putIfAbsent(exampleId, step);
    }

    public Node.SkipResult shouldBeSkipped(UniqueId exampleId) {
        Step failedStep = firstFailedStepByExampleId.get(exampleId);
        final Node.SkipResult skipResult;
        if (failedStep != null) {
            skipResult = Node.SkipResult.skip("Previous failure at step: " + failedStep.getLine());
        } else {
            skipResult = Node.SkipResult.doNotSkip();
        }
        return skipResult;
    }

    public void cleanUpExample(UniqueId exampleId) {
        firstFailedStepByExampleId.remove(exampleId);
    }
}
