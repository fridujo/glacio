package com.github.fridujo.glacio.parsing.i18n;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.github.fridujo.glacio.ast.Position;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class GherkinLanguages implements Languages {

    private final Map<String, LanguageKeywords> languageKeywordsByLanguages;

    private GherkinLanguages(Map<String, LanguageKeywords> languageKeywordsByLanguages) {
        this.languageKeywordsByLanguages = languageKeywordsByLanguages;
    }

    public static Languages load() throws IllegalStateException {
        try {
            try (InputStream inputStream = GherkinLanguages.class.getClassLoader().getResourceAsStream("gherkin-languages.json")) {
                JsonObject jsonObject = Json.parse(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))).asObject();
                return new GherkinLanguages(new GherkinJsonMapper().map(jsonObject));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read Gherkin languages file");
        }
    }

    public LanguageKeywords get(Position position, String language) throws LanguageNotFoundException {
        if (!languageKeywordsByLanguages.containsKey(language)) {
            throw new LanguageNotFoundException(position.asParsingPosition(), language, languageKeywordsByLanguages.keySet());
        }
        return languageKeywordsByLanguages.get(language);
    }
}
