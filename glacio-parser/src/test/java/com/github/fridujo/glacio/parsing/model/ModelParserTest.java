package com.github.fridujo.glacio.parsing.model;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tool.Resource;

import com.github.fridujo.glacio.model.DataTable;
import com.github.fridujo.glacio.model.DocString;
import com.github.fridujo.glacio.model.Example;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.model.Keyword;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.model.StepArgument;
import com.github.fridujo.glacio.parsing.i18n.GherkinLanguages;

class ModelParserTest {

    @Test
    void nominal_parsing() {
        StringSource source = Resource.load("parsing_glacio_sample.feature");
        ModelParser modelParser = new ModelParser(GherkinLanguages.load());

        Feature feature = modelParser.parse(source);

        assertThat(feature.getSourceURI()).isEqualTo(source.getURI());
        assertThat(feature.getName()).isEqualTo("User login");
        assertThat(feature.getExamples())
            .hasSize(3)
            .extracting(Example::getName)
            .containsExactly(
                "Successful login",
                "Successful login using template",
                "Successful login using template");
        assertThat(feature.getExamples())
            .extracting(Example::getParameters)
            .containsExactly(
                emptyMap(),
                singletonMap("browser_type", "Firefox"),
                singletonMap("browser_type", "Chrome"));

        Step setupStep = feature.getExamples().get(0).getSteps().get(0);
        assertThat(setupStep.isBackground()).isTrue();
        assertThat(setupStep.getKeyword()).isPresent();
        assertThat(setupStep.getKeyword().get().getType()).isEqualTo(Keyword.Type.GIVEN);
        assertThat(setupStep.getKeyword().get().getLiteral()).isEqualTo("Given ");
        assertThat(setupStep.getText()).isEqualTo("a test setup");
        assertThat(setupStep.getLine()).isEqualTo("Given a test setup (background)");

        Step sqlStep = feature.getExamples().get(0).getSteps().get(2).getSubsteps().get(0).getSubsteps().get(2);
        assertThat(sqlStep.isBackground()).isFalse();
        assertThat(sqlStep.getArgument()).isPresent().containsInstanceOf(DocString.class);
        assertThat(sqlStep.getArgument().get().getType()).isEqualTo(StepArgument.Type.DOC_STRING);
        DocString sqlStatement = (DocString) sqlStep.getArgument().get();
        assertThat(sqlStatement.getContentType()).contains("sql");
        assertThat(sqlStatement.getContent()).startsWith("INSERT INTO USERS");

        Step navigateToStep = feature.getExamples().get(0).getSteps().get(3).getSubsteps().get(1);
        assertThat(navigateToStep.getArgument()).isPresent().containsInstanceOf(DataTable.class);
        assertThat(navigateToStep.getArgument().get().getType()).isEqualTo(StepArgument.Type.DATA_TABLE);
        DataTable dataTable = (DataTable) navigateToStep.getArgument().get();
        assertThat(dataTable.getRows().get(0).getCells()).containsExactly("protocol", "address", "path");
        assertThat(dataTable.getRows().get(1).getCells()).containsExactly("https", "www.yourapp.com", "/login");
    }
}
