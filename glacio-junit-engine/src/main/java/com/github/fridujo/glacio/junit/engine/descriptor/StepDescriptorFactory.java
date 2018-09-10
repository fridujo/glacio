package com.github.fridujo.glacio.junit.engine.descriptor;

import com.github.fridujo.glacio.model.Step;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

public class StepDescriptorFactory {
    public static TestDescriptor create(UniqueId exampleId, UniqueId parentUniqueId, Step step) {
        if (step.getSubsteps().isEmpty()) {
            return new LeafStepDescriptor(exampleId, parentUniqueId, step);
        } else {
            return new ContainerStepDescriptor(exampleId, parentUniqueId, step);
        }
    }
}
