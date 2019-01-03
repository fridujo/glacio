package com.github.fridujo.glacio.junit.engine.descriptor;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.model.Step;

public class StepDescriptorFactory {
    public TestDescriptor create(UniqueId configurationId, UniqueId exampleId, UniqueId parentUniqueId, Step step) {
        if (step.getSubsteps().isEmpty()) {
            return new LeafStepDescriptor(configurationId, exampleId, parentUniqueId, step);
        } else {
            return new StepContainerDescriptor(configurationId, exampleId, parentUniqueId, step);
        }
    }
}
