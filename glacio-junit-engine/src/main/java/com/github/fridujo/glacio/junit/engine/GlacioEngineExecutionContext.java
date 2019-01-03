package com.github.fridujo.glacio.junit.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.junit.platform.engine.support.hierarchical.Node;

import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.runtime.configuration.ConfigurationContext;
import com.github.fridujo.glacio.running.runtime.glue.ExecutableLookup;

public class GlacioEngineExecutionContext implements EngineExecutionContext {

    private final Map<UniqueId, Step> firstFailedStepByExampleId = new ConcurrentHashMap<>();
    private final Map<UniqueId, ConfigurationContextWrapper> configurationContexts = new ConcurrentHashMap<>();

    public ExecutableLookup getExecutableLookup(UniqueId configurationId) {
        return configurationContexts.get(configurationId).executableLookup;
    }

    public GlacioEngineExecutionContext initializeConfiguration(UniqueId configurationId,
                                                                ExecutableLookup executableLookup,
                                                                ConfigurationContext configurationContext,
                                                                ExtensionContext extensionContext) {
        configurationContexts.put(configurationId,
            new ConfigurationContextWrapper(executableLookup, configurationContext, extensionContext));
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

    public GlacioEngineExecutionContext notifyBeforeExample(UniqueId configurationId) {
        ConfigurationContextWrapper configurationContextWrapper = configurationContexts.get(configurationId);
        configurationContextWrapper.configurationContext.beforeExample(configurationContextWrapper.extensionContext);
        return this;
    }

    public void cleanUpExample(UniqueId exampleId) {
        firstFailedStepByExampleId.remove(exampleId);
    }

    private static class ConfigurationContextWrapper {
        private final ExecutableLookup executableLookup;
        private final ConfigurationContext configurationContext;
        private final ExtensionContext extensionContext;

        private ConfigurationContextWrapper(ExecutableLookup executableLookup,
                                            ConfigurationContext configurationContext,
                                            ExtensionContext extensionContext) {
            this.executableLookup = executableLookup;
            this.configurationContext = configurationContext;
            this.extensionContext = extensionContext;
        }
    }
}
