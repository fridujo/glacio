package com.github.fridujo.glacio.junit.engine.descriptor;

import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.model.Step;

public class StepContainerDescriptor extends AbstractGlacioTestDescriptor {

    private final StepDescriptorFactory stepDescriptorFactory = new StepDescriptorFactory();

    public StepContainerDescriptor(UniqueId exampleId, UniqueId parentUniqueId, Step step) {
        super(parentUniqueId.append("step", step.getText()), step.getLine());

        step.getSubsteps().forEach(sub -> addChild(stepDescriptorFactory.create(exampleId, getUniqueId(), sub)));
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }
}
