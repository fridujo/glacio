package com.github.fridujo.glacio.ast;

public interface Visitor {
    default void visitFeature(Feature feature) {
        visitPositioned(feature);
    }

    default void visitLanguage(PositionedString language) {
        visitPositioned(language);
    }

    default void visitBackground(Background background) {
        visitPositioned(background);
    }

    default void visitScenario(Scenario scenario) {
        visitPositioned(scenario);
    }

    default void visitScenarioOutline(ScenarioOutline scenarioOutline) {
        visitPositioned(scenarioOutline);
    }

    default void visitStep(Step step) {
        visitPositioned(step);
    }

    default void visitDocString(DocString docString) {
        visitPositioned(docString);
    }

    default void visitDataTable(DataTable dataTable) {
        visitPositioned(dataTable);
    }

    default void visitPositioned(Positioned positioned) {
    }
}
