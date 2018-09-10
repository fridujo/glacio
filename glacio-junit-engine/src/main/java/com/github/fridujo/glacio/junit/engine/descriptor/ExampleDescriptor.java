package com.github.fridujo.glacio.junit.engine.descriptor;

import com.github.fridujo.glacio.junit.engine.GlacioEngineExecutionContext;
import com.github.fridujo.glacio.model.Example;
import org.junit.platform.engine.UniqueId;

import java.util.stream.Collectors;

public class ExampleDescriptor extends GlacioTestDescriptor {
    public static final String SEGMENT_TYPE = "example";

    public ExampleDescriptor(UniqueId parentUniqueId, Example example) {
        super(parentUniqueId.append(SEGMENT_TYPE, example.getName() + example.getParameters().hashCode()), displayName(example));

        example.getSteps().forEach(step -> addChild(StepDescriptorFactory.create(getUniqueId(), getUniqueId(), step)));
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

    @Override
    public GlacioEngineExecutionContext prepare(GlacioEngineExecutionContext context) {
        context.getEventAware().beforeExample();
        return context;
    }

    @Override
    public void cleanUp(GlacioEngineExecutionContext context) {
        context.getEventAware().afterExample();
    }
}
