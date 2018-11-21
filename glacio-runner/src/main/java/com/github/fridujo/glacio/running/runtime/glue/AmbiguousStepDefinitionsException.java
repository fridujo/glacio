package com.github.fridujo.glacio.running.runtime.glue;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.GlacioRunnerException;

public class AmbiguousStepDefinitionsException extends GlacioRunnerException {
    public AmbiguousStepDefinitionsException(Step step, Set<Map.Entry<Pattern, Method>> matches) {
        super(createMessage(step, matches));
    }

    private static String createMessage(Step step, Set<Map.Entry<Pattern, Method>> matches) {
        return "Step '" + step.getText() + "' matches multiple Step Definitions: " + matches
            .stream()
            .map(e -> e.getValue())
            .map(m -> m.getDeclaringClass().getSimpleName() + "#" + m.getName())
            .collect(Collectors.joining(", "));
    }
}
