package com.github.fridujo.glacio.running;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.fridujo.glacio.running.runtime.RuntimeOptions;

public class RuntimeOptionsParser {

    private final ArrayList<String> args;

    public RuntimeOptionsParser(List<String> argsList) {
        this.args = new ArrayList<>(argsList);
    }

    public RuntimeOptions parse() {
        Set<String> parsedGlue = new HashSet<>();
        Set<String> parsedFeaturePaths = new HashSet<>();
        while (!args.isEmpty()) {
            String arg = args.remove(0).trim();
            if ("--glue".equals(arg) || "-g".equals(arg)) {
                String gluePath = args.remove(0);
                parsedGlue.add(gluePath);
            } else {
                parsedFeaturePaths.add(arg);
            }
        }
        return new RuntimeOptions(parsedFeaturePaths, parsedGlue);
    }
}
