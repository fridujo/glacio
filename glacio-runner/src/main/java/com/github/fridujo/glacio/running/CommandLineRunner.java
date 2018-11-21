package com.github.fridujo.glacio.running;

import java.util.Arrays;
import java.util.List;

import com.github.fridujo.glacio.running.runtime.Runtime;
import com.github.fridujo.glacio.running.runtime.RuntimeOptions;
import com.github.fridujo.glacio.running.runtime.feature.FeatureLoader;
import com.github.fridujo.glacio.running.runtime.feature.FileFeatureLoader;
import com.github.fridujo.glacio.running.runtime.io.MultiLoader;

public abstract class CommandLineRunner {
    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        RuntimeOptions runtimeOptions = new RuntimeOptionsParser(argsList).parse();

        FeatureLoader featureLoader = new FileFeatureLoader(new MultiLoader(Runtime.class.getClassLoader()));

        Runtime runtime = new Runtime(runtimeOptions, featureLoader);
        runtime.run();
    }
}
