package com.github.fridujo.glacio.junit.engine.descriptor;

import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.model.Step;

public class StepContainerDescriptor extends AbstractGlacioTestDescriptor {

    private final StepDescriptorFactory stepDescriptorFactory = new StepDescriptorFactory();

    public StepContainerDescriptor(UniqueId configurationId, UniqueId exampleId, UniqueId parentUniqueId, Step step) {
        super(configurationId, parentUniqueId.append("step", step.getText()), step.getLine());

        step.getSubsteps().forEach(sub -> addChild(stepDescriptorFactory.create(configurationId, exampleId, getUniqueId(), sub)));
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }
}
