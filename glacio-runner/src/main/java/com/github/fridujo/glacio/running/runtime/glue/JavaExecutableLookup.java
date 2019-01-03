package com.github.fridujo.glacio.running.runtime.glue;

import static java.util.function.Function.identity;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.running.api.Given;
import com.github.fridujo.glacio.running.api.Then;
import com.github.fridujo.glacio.running.api.When;
import com.github.fridujo.glacio.running.api.extension.BeforeExampleCallback;
import com.github.fridujo.glacio.running.api.extension.ExtensionContext;
import com.github.fridujo.glacio.running.runtime.convert.MethodParameterConverter;
import com.github.fridujo.glacio.running.runtime.io.ClassFinder;
import com.github.fridujo.glacio.running.runtime.io.MultiLoader;
import com.github.fridujo.glacio.running.runtime.io.ResourceLoaderClassFinder;

public class JavaExecutableLookup implements ExecutableLookup, BeforeExampleCallback {

    /**
     * Visible for test.
     */
    final GlueFactory glueFactory;
    private final Map<Pattern, Method> methodsByPattern;
    private final MethodParameterConverter methodParameterConverter = new MethodParameterConverter();

    public JavaExecutableLookup(ClassLoader classLoader,
                                Set<String> gluePaths) {
        glueFactory = new GlueFactory();

        ClassFinder classFinder = new ResourceLoaderClassFinder(new MultiLoader(classLoader), classLoader);
        methodsByPattern = gluePaths
            .stream()
            .flatMap(gluePath -> classFinder.getDescendants(Object.class, gluePath).stream())
            .flatMap(glueClass -> Arrays.stream(glueClass.getDeclaredMethods()))
            .filter(this::isStepDefMethod)
            .collect(Collectors.toMap(m -> getAssociatedPattern(m), identity()));
    }

    private Pattern getAssociatedPattern(Method m) {
        Given given = m.getAnnotation(Given.class);
        When when = m.getAnnotation(When.class);
        Then then = m.getAnnotation(Then.class);
        String regex;
        if (given != null) {
            regex = given.value();
        } else if (when != null) {
            regex = when.value();
        } else {
            regex = then.value();
        }
        return Pattern.compile(regex);
    }

    private boolean isStepDefMethod(Method method) {
        return
            method.isAnnotationPresent(Given.class)
                || method.isAnnotationPresent(When.class)
                || method.isAnnotationPresent(Then.class);
    }

    @Override
    public Executable lookup(Step step) throws MissingStepImplementationException, AmbiguousStepDefinitionsException {
        Set<Map.Entry<Pattern, Method>> matches = new LinkedHashSet<>();
        for (Map.Entry<Pattern, Method> potentialMatch : methodsByPattern.entrySet()) {
            Matcher matcher = potentialMatch.getKey().matcher(step.getText());
            if (matcher.matches()) {
                matches.add(potentialMatch);
            }
        }
        if (matches.isEmpty()) {
            throw new MissingStepImplementationException(step);
        } else if (matches.size() > 1) {
            throw new AmbiguousStepDefinitionsException(step, matches);
        } else {
            Map.Entry<Pattern, Method> match = matches.iterator().next();
            Method method = match.getValue();
            return new JavaExecutable(glueFactory, methodParameterConverter, step, match.getKey(), method);
        }
    }

    @Override
    public void beforeExample(ExtensionContext context) {
        glueFactory.reset();
    }
}
