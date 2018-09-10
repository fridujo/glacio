package com.github.fridujo.glacio.running;

import com.github.fridujo.glacio.running.runtime.Runtime;
import com.github.fridujo.glacio.running.runtime.RuntimeOptions;
import com.github.fridujo.glacio.running.runtime.feature.FeatureLoader;
import com.github.fridujo.glacio.running.runtime.feature.FileFeatureLoader;
import com.github.fridujo.glacio.running.runtime.glue.GlueFactory;
import com.github.fridujo.glacio.running.runtime.glue.SimpleGlueFactory;
import com.github.fridujo.glacio.running.runtime.io.MultiLoader;
import com.github.fridujo.glacio.running.runtime.reporting.ConsoleReporter;
import com.github.fridujo.glacio.running.runtime.reporting.Reporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandLineRunner {

    public static void main(String[] args) {
        ArrayList<String> argsList = new ArrayList<>();
        Arrays.stream(args).forEach(argsList::add);
        RuntimeOptions runtimeOptions = parse(argsList);
        // TODO mechanism to load different glueFactory when specified
        GlueFactory glueFactory = new SimpleGlueFactory();
        FeatureLoader featureLoader = new FileFeatureLoader(new MultiLoader(Runtime.class.getClassLoader()));
        // TODO mechanism to load specified reporters
        Reporter reporter = new ConsoleReporter();
        Runtime runtime = new Runtime(runtimeOptions, glueFactory, featureLoader, reporter);
        runtime.run();
    }

    private static RuntimeOptions parse(List<String> args) {
        Set<String> parsedGlue = new HashSet<>();
        Set<String> parsedFeaturePaths = new HashSet<>();
        while (!args.isEmpty()) {
            String arg = args.remove(0).trim();
            if (arg.equals("--glue") || arg.equals("-g")) {
                String gluePath = args.remove(0);
                parsedGlue.add(gluePath);
            } else {
                parsedFeaturePaths.add(arg);
            }
        }
        return new RuntimeOptions(parsedFeaturePaths, parsedGlue);
    }
}
