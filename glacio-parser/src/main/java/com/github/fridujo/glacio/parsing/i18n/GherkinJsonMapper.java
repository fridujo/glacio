package com.github.fridujo.glacio.parsing.i18n;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.eclipsesource.json.JsonObject;

public class GherkinJsonMapper {

    public Map<String, LanguageKeywords> map(JsonObject jsonObject) {
        List<String> languageNames = jsonObject.names();

        return languageNames.stream().collect(Collectors.toMap(
            Function.identity(),
            k -> mapToLanguage(jsonObject.get(k).asObject())
        ));
    }

    private LanguageKeywords mapToLanguage(JsonObject jsonObject) {
        String name = jsonObject.get("name").asString();
        Set<String> feature = getKeywords(jsonObject, "feature");
        Set<String> scenarioOutline = getKeywords(jsonObject, "scenarioOutline");
        Set<String> background = getKeywords(jsonObject, "background");
        Set<String> scenario = getKeywords(jsonObject, "scenario");
        Set<String> given = getKeywords(jsonObject, "given");
        Set<String> when = getKeywords(jsonObject, "when");
        Set<String> then = getKeywords(jsonObject, "then");
        Set<String> and = getKeywords(jsonObject, "and");
        Set<String> but = getKeywords(jsonObject, "but");
        Set<String> examples = getKeywords(jsonObject, "examples");

        return new LanguageKeywords(
            name,
            feature,
            background,
            scenarioOutline,
            scenario,
            given,
            when,
            then,
            concat(and, but),
            examples);
    }

    private Set<String> concat(Set<String> s1, Set<String> s2) {
        Set<String> result = new LinkedHashSet<>();
        result.addAll(s1);
        result.addAll(s2);
        return result;
    }

    private Set<String> getKeywords(JsonObject jsonObject, String keywordName) {
        return jsonObject
            .get(keywordName)
            .asArray()
            .values()
            .stream()
            .map(jv -> jv.asString())
            .filter(k -> !"* ".equals(k))
            .collect(Collectors.toSet());
    }
}
