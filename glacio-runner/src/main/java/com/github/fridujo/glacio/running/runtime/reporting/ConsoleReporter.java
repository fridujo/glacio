package com.github.fridujo.glacio.running.runtime.reporting;

import com.github.fridujo.glacio.model.Example;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.model.Keyword;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.parsing.tool.Strings;
import com.github.fridujo.glacio.running.runtime.ExecutionResult;

import java.io.PrintStream;
import java.util.stream.Collectors;

public class ConsoleReporter implements Reporter {

    private final PrintStream printStream = System.out;
    private ExecutionResult lastExecutionResult;

    @Override
    public String getName() {
        return "console";
    }

    @Override
    public void feature(Feature feature) {
        printStream.print("Feature: " + feature.getName());
    }

    @Override
    public void example(Example example) {
        printLastStatus();
        String parameterDescription;
        if (!example.getParameters().isEmpty()) {
            parameterDescription = example
                .getParameters()
                .entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ", " (", ")"));
        } else {
            parameterDescription = "";
        }
        printStream.println("\n    Example: " + example.getName() + parameterDescription);
    }

    @Override
    public void step(int level, Step step) {
        printLastStatus();
        String backgroundDescription = step.isBackground() ? "(background) " : "";
        String keyword = step.getKeyword().map(Keyword::getLiteral).orElse("");
        printStream.print(Strings.repeat(' ', 4 + level * 4) + backgroundDescription + keyword + step.getText());
    }

    @Override
    public void result(ExecutionResult executionResult) {
        lastExecutionResult = executionResult;
    }

    @Override
    public void done() {
        printLastStatus();
    }

    private void printLastStatus() {
        if (lastExecutionResult != null) {
            printStream.println(" .... " + lastExecutionResult.getStatus().name());
            lastExecutionResult = null;
        } else {
            printStream.print("\n");
        }
    }
}
