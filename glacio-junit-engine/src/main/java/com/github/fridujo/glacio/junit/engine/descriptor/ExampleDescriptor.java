package com.github.fridujo.glacio.junit.engine.descriptor;

import java.util.stream.Collectors;

import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.model.Example;

public class ExampleDescriptor extends AbstractGlacioTestDescriptor {

    public static final String SEGMENT_TYPE = "example";

    private final StepDescriptorFactory stepDescriptorFactory = new StepDescriptorFactory();

    public ExampleDescriptor(UniqueId configurationId, UniqueId parentUniqueId, Example example) {
        super(configurationId, parentUniqueId.append(SEGMENT_TYPE, example.getName() + example.getParameters().hashCode()), displayName(example));

        example.getSteps().forEach(step -> addChild(stepDescriptorFactory.create(configurationId, getUniqueId(), getUniqueId(), step)));
    }

    private static String displayName(Example example) {
        String parametersDescription = example.getParameters().size() > 0 ?
            example
                .getParameters()
                .entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ", " (", ")"))
            : "";
        return "Example: " + example.getName() + parametersDescription;
    }

    @Override
    public Type getType() {
        return Type.CONTAINER;
    }

    public void cleanUp(GlacioEngineExecutionContext context) {
        context.cleanUpExample(getUniqueId());
    }
}
