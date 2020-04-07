package com.github.fridujo.glacio.parsing.i18n;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.ast.Position;

class GherkinLanguagesTest {

    @Test
    void default_language_loading() {
        LanguageKeywords languageKeywords = GherkinLanguages.load().defaultLanguage();

        assertThat(languageKeywords.getLanguageName()).isEqualTo("English");
        assertThat(languageKeywords.getFeature()).contains("Feature");
        assertThat(languageKeywords.getBackground()).contains("Background");
        assertThat(languageKeywords.getScenarioOutline()).contains("Scenario Outline");
        assertThat(languageKeywords.getScenario()).contains("Scenario");
        assertThat(languageKeywords.getGiven()).contains("Given ");
        assertThat(languageKeywords.getWhen()).contains("When ");
        assertThat(languageKeywords.getThen()).contains("Then ");
        assertThat(languageKeywords.getAnd()).contains("And ", "But ");
        assertThat(languageKeywords.getExamples()).contains("Examples");
    }

    @Test
    void unknown_language_throws() {
        assertThatExceptionOfType(LanguageNotFoundException.class)
            .isThrownBy(() -> GherkinLanguages.load().get(new Position(2, 4), "test"))
            .withMessageContaining("Cannot find language test, available ones are:")
            .withMessageContaining("at [line=2, column=4]");
    }

    @Test
    void emptyLanguage_does_not_contain_any_keyword() {
        LanguageKeywords emptyLanguage = LanguageKeywords.empty();

        assertThat(emptyLanguage.getFeature()).isEmpty();
        assertThat(emptyLanguage.getBackground()).isEmpty();
        assertThat(emptyLanguage.getScenarioOutline()).isEmpty();
        assertThat(emptyLanguage.getScenario()).isEmpty();
        assertThat(emptyLanguage.getGiven()).isEmpty();
        assertThat(emptyLanguage.getWhen()).isEmpty();
        assertThat(emptyLanguage.getThen()).isEmpty();
        assertThat(emptyLanguage.getAnd()).isEmpty();
        assertThat(emptyLanguage.getExamples()).isEmpty();
    }
}
