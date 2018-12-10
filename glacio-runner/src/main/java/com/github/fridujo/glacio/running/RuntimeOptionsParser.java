package com.github.fridujo.glacio.running;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.fridujo.glacio.running.runtime.GlacioRunnerInitializationException;
import com.github.fridujo.glacio.running.runtime.RuntimeOptions;

public class RuntimeOptionsParser {

    private final ArrayList<String> args;

    public RuntimeOptionsParser(List<String> argsList) {
        this.args = new ArrayList<>(argsList);
    }

    public RuntimeOptions parse() {
        Set<String> parsedGlue = new LinkedHashSet<>();
        Set<String> parsedFeaturePaths = new LinkedHashSet<>();
        Set<Class<?>> configurationClasses = new LinkedHashSet<>();
        while (!args.isEmpty()) {
            String arg = args.remove(0).trim();
            if ("--glue".equals(arg) || "-g".equals(arg)) {
                String gluePath = args.remove(0);
                parsedGlue.add(gluePath);
            } else if ("--configuration-class".equals(arg) || "-cc".equals(arg)) {
                String configurationClassName = args.remove(0);
                configurationClasses.add(toClass(configurationClassName));
            } else {
                parsedFeaturePaths.add(arg);
            }
        }
        return new RuntimeOptions(parsedFeaturePaths, parsedGlue, configurationClasses);
    }

    private <T> Class<? extends T> toClass(String className) {
        try {
            return (Class<? extends T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new GlacioRunnerInitializationException("Initializer[" + className + "] not found", e);
        }
    }
}
