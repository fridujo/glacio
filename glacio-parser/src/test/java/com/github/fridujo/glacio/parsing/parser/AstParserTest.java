package com.github.fridujo.glacio.parsing.parser;

import com.github.fridujo.glacio.ast.DataTable;
import com.github.fridujo.glacio.ast.Feature;
import com.github.fridujo.glacio.ast.Scenario;
import com.github.fridujo.glacio.ast.ScenarioOutline;
import com.github.fridujo.glacio.ast.Step;
import com.github.fridujo.glacio.ast.TableCell;
import com.github.fridujo.glacio.ast.Tag;
import com.github.fridujo.glacio.parsing.ParsingException;
import com.github.fridujo.glacio.parsing.charstream.CharStream;
import com.github.fridujo.glacio.parsing.i18n.GherkinLanguages;
import com.github.fridujo.glacio.parsing.lexer.Lexer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tool.Resource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class AstParserTest {

    static Stream<Arguments> missing_token_data_set() {
        return Stream.of(
            Arguments.of("", "Expecting FEATURE token (any of: [Ability, Business Need, Feature])"),
            Arguments.of("Ability ", "Expecting COLON token (character ':')"),
            Arguments.of("Ability:\nBackground ", "Expecting COLON token (character ':')"),
            Arguments.of("Feature:\nScenario ", "Expecting COLON token (character ':')"),
            Arguments.of("Feature:\nScenario Outline ", "Expecting COLON token (character ':')"),
            Arguments.of("Feature:\nScenario Outline:\n Examples ", "Expecting COLON token (character ':')"),
            Arguments.of("Feature:\n Given something", "Expecting SCENARIO token (any of: [Example, Scenario]) or SCENARIO_OUTLINE token (any of: [Scenario Outline, Scenario Template])"),
            Arguments.of("Feature:\nScenario Outline:\n Given something ", "Expecting EXAMPLES token (any of: [Scenarios, Examples])"),
            Arguments.of("Feature:\nScenario Outline:\n Examples:\n | test ", "Expecting TABLE_DELIMITER token (character '|')")
        );
    }

    @Test
    void nominal_parsing() {
        String text = Resource.load("parsing_glacio_sample.feature").getContent();

        Feature feature = parseFeature(text);

        assertThat(feature).isNotNull();
        assertThat(feature.getTags()).extracting(Tag::getName).containsExactly("tag1", "tag2");
        assertThat(feature.getDescription()).contains("Free form description\ncan be placed here");
        assertThat(feature.getBackground()).isPresent();
        assertThat(feature.getBackground().get().getSteps()).hasSize(2);
        assertThat(feature.getBackground().get().getSteps().get(1).getSubsteps()).extracting(Step::getText).containsExactly("with a substep");
        assertThat(feature.getScenarios()).hasSize(2);
        assertThat(feature.getScenarios().get(0)).isExactlyInstanceOf(Scenario.class);
        assertThat(feature.getScenarios().get(0)
            .getSteps().get(0) // Given a user with valid credentials
            .getSubsteps().get(0) // Insert generated user with valid credentials in database
            .getSubsteps().get(2) // Execute SQL
            .getDocString().get().getContentType()
        ).contains("sql");
        assertThat(feature.getScenarios().get(1)).isExactlyInstanceOf(ScenarioOutline.class);
    }

    @ParameterizedTest
    @MethodSource("missing_token_data_set")
    void missing_token_throws(String text, String exceptionMessage) {
        assertThatExceptionOfType(MissingTokenException.class)
            .isThrownBy(() -> parseFeature(text))
            .withMessageContaining(exceptionMessage);
    }

    @Test
    void and_keyword_before_any_given_when_or_then_throws() {
        assertThatExceptionOfType(ParsingException.class)
            .isThrownBy(() -> parseFeature("Feature:\nScenario:\n  And something "))
            .withMessageContaining("Found AND keyword before any GIVEN, WHEN or THEN one at [line=3, column=2]");
    }

    @Test
    void parsing_data_table() {
        Feature feature = parseFeature("Feature:\nScenario:\nGiven something\n| h 1 | h2 |\n|v1|v  2  |");

        DataTable dataTable = feature.getScenarios().get(0).getSteps().get(0).getDataTable().get();
        assertThat(dataTable.getRows()).hasSize(2);
        assertThat(dataTable.getRows().get(0).getCells()).extracting(TableCell::getValue).containsExactly("h 1", "h2");
        assertThat(dataTable.getRows().get(1).getCells()).extracting(TableCell::getValue).containsExactly("v1", "v  2");
    }

    private Feature parseFeature(String text) {
        Lexer lexer = new Lexer(new CharStream(text));
        AstParser astParser = new AstParser(lexer, GherkinLanguages.load());

        return astParser.parseFeature();
    }
}
