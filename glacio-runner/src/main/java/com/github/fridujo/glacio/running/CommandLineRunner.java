package com.github.fridujo.glacio.running;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.github.fridujo.glacio.running.runtime.Runtime;
import com.github.fridujo.glacio.running.runtime.RuntimeOptions;
import com.github.fridujo.glacio.running.runtime.configuration.ConfigurationContext;
import com.github.fridujo.glacio.running.runtime.configuration.ConfigurationContextBuilder;

public abstract class CommandLineRunner {
    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        RuntimeOptions runtimeOptions = new RuntimeOptionsParser(argsList).parse();
        ConfigurationContextBuilder configurationContextBuilder = new ConfigurationContextBuilder();
        Set<ConfigurationContext> configurationContexts = configurationContextBuilder.fromRuntimeOptions(runtimeOptions);

        Runtime runtime = new Runtime(configurationContexts);
        runtime.run();
    }
}
