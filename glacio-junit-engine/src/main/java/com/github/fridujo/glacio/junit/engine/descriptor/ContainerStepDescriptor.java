package com.github.fridujo.glacio.junit.engine.descriptor;

import com.github.fridujo.glacio.model.Step;
import org.junit.platform.engine.UniqueId;

public class ContainerStepDescriptor extends GlacioTestDescriptor {

    private final UniqueId exampleId;

    public ContainerStepDescriptor(UniqueId exampleId, UniqueId parentUniqueId, Step step) {
        super(parentUniqueId.append("step", step.getText()), step.getLine());
        this.exampleId = exampleId;

        step.getSubsteps().forEach(sub -> addChild(StepDescriptorFactory.create(exampleId, getUniqueId(), sub)));
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }
}
