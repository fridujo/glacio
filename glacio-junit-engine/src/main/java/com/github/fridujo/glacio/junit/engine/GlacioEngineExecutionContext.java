package com.github.fridujo.glacio.junit.engine;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.junit.platform.engine.support.hierarchical.Node;

import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.runtime.BeforeExampleEventAware;
import com.github.fridujo.glacio.running.runtime.GlacioRunnerInitializationException;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;

public class GlacioEngineExecutionContext implements EngineExecutionContext {

    private final Map<UniqueId, Step> firstFailedStepByExampleId = new ConcurrentHashMap<>();
    private ExecutableLookup executableLookup;
    private Optional<BeforeExampleEventAware> beforeExampleEventAware;

    public ExecutableLookup getExecutableLookup() {
        if (executableLookup == null) {
            throw new GlacioRunnerInitializationException("No " + ExecutableLookup.class.getSimpleName() + " supplied");
        }
        return executableLookup;
    }

    public GlacioEngineExecutionContext setExecutableLookup(ExecutableLookup executableLookup) {
        this.executableLookup = executableLookup;
        if (executableLookup instanceof BeforeExampleEventAware) {
            beforeExampleEventAware = Optional.of((BeforeExampleEventAware) executableLookup);
        } else {
            beforeExampleEventAware = Optional.empty();
        }
        return this;
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

    public void notifyBeforeExample() {
        beforeExampleEventAware.ifPresent(BeforeExampleEventAware::beforeExample);
    }

    public void cleanUpExample(UniqueId exampleId) {
        firstFailedStepByExampleId.remove(exampleId);
    }
}
