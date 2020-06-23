package com.github.fridujo.glacio.parsing.model;

import java.io.UncheckedIOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.fridujo.glacio.ast.Background;
import com.github.fridujo.glacio.ast.KeywordType;
import com.github.fridujo.glacio.ast.PositionedString;
import com.github.fridujo.glacio.ast.RootStep;
import com.github.fridujo.glacio.ast.Scenario;
import com.github.fridujo.glacio.ast.ScenarioOutline;
import com.github.fridujo.glacio.ast.TableRow;
import com.github.fridujo.glacio.ast.Tag;
import com.github.fridujo.glacio.model.DataTable;
import com.github.fridujo.glacio.model.DocString;
import com.github.fridujo.glacio.model.Example;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.model.Keyword;
import com.github.fridujo.glacio.model.Language;
import com.github.fridujo.glacio.model.Step;
import com.github.fridujo.glacio.model.StepArgument;
import com.github.fridujo.glacio.parsing.charstream.CharStream;
import com.github.fridujo.glacio.parsing.i18n.LanguageKeywords;
import com.github.fridujo.glacio.parsing.i18n.Languages;
import com.github.fridujo.glacio.parsing.lexer.Lexer;
import com.github.fridujo.glacio.parsing.parser.AstParser;

public class ModelParser {

    private final String GLACIO_PLACEHOLDER_START = "<";
    private final String GLACIO_PLACEHOLDER_END = ">";
    private final String GLACIO_PLACEHOLDER_TEMPLATE = GLACIO_PLACEHOLDER_START + "%s" + GLACIO_PLACEHOLDER_END;
    private final Pattern GLACIO_PLACEHOLDERS_PATTERN = Pattern.compile(GLACIO_PLACEHOLDER_START + "([^>]+)" + GLACIO_PLACEHOLDER_END);
    private final Languages languages;

    public ModelParser(Languages languages) {
        this.languages = languages;
    }

    public Feature parse(StringSource stringSource) throws UncheckedIOException {
        CharStream charStream = new CharStream(stringSource.getContent());
        Lexer lexer = new Lexer(charStream);
        AstParser astParser = new AstParser(lexer, languages);
        com.github.fridujo.glacio.ast.Feature astFeature = astParser.parseFeature();

        return mapToFeature(stringSource.getURI(), astFeature, languages);
    }

    private Feature mapToFeature(URI uri, com.github.fridujo.glacio.ast.Feature astFeature, Languages languages) {
        return new Feature(uri, astFeature.getName(), mapToLanguage(astFeature.getLanguage(), languages), mapToExamples(astFeature));
    }

    private Language mapToLanguage(Optional<PositionedString> language, Languages languages) {
        LanguageKeywords languageKeywords = language.map(l -> languages.get(l.getPosition(), l.getValue())).orElse(languages.defaultLanguage());

        return new Language(languageKeywords.getCode(), languageKeywords.getLanguageName(), languageKeywords.getNativeName());
    }

    private List<Example> mapToExamples(com.github.fridujo.glacio.ast.Feature astFeature) {
        List<Step> backgroundSteps = astFeature.getBackground()
            .map(Background::getSteps)
            .orElse(Collections.emptyList())
            .stream()
            .map(s -> mapToStep(s, true, Collections.emptyMap()))
            .collect(Collectors.toList());

        Set<String> featureTags = mapTags(astFeature.getTags());
        return astFeature.getScenarios()
            .stream()
            .map(s -> mapToExamples(s, backgroundSteps, featureTags))
            .flatMap(sl -> sl.stream())
            .collect(Collectors.toList());
    }

    private List<Example> mapToExamples(Scenario scenario, List<Step> backgroundSteps, Set<String> featureTags) {
        if (scenario instanceof ScenarioOutline) {
            return mapFromScenarioOutline((ScenarioOutline) scenario, backgroundSteps, featureTags);
        } else {
            return mapFromScenario(scenario, backgroundSteps, featureTags);
        }
    }

    private List<Example> mapFromScenario(Scenario scenario, List<Step> backgroundSteps, Set<String> featureTags) {
        List<Step> scenarioSteps = scenario
            .getSteps()
            .stream()
            .map(s -> mapToStep(s, false, Collections.emptyMap()))
            .collect(Collectors.toList());

        Set<String> tags = new HashSet<>();
        tags.addAll(featureTags);
        tags.addAll(mapTags(scenario.getTags()));
        List<Step> steps = new ArrayList<>();
        steps.addAll(backgroundSteps);
        steps.addAll(scenarioSteps);
        return Collections.singletonList(new Example(scenario.getName(), Collections.emptyMap(), steps, tags));
    }

    private List<Example> mapFromScenarioOutline(ScenarioOutline scenarioOutline, List<Step> backgroundSteps, Set<String> featureTags) {
        TableRow header = scenarioOutline.getExamples().getHeader();

        List<Map<String, String>> parametersList = scenarioOutline.getExamples().getBody()
            .stream()
            .map(parameterValues -> buildParameters(header, parameterValues))
            .collect(Collectors.toList());

        Set<String> tags = new HashSet<>();
        tags.addAll(featureTags);
        tags.addAll(mapTags(scenarioOutline.getTags()));

        List<Example> examples = new ArrayList<>();
        for (Map<String, String> parameters : parametersList) {
            List<Step> scenarioSteps = scenarioOutline
                .getSteps()
                .stream()
                .map(s -> mapToStep(s, false, parameters))
                .collect(Collectors.toList());
            String name = resolveVariables(scenarioOutline.getName(), parameters);
            List<Step> steps = new ArrayList<>();
            steps.addAll(backgroundSteps);
            steps.addAll(scenarioSteps);
            examples.add(new Example(name, parameters, steps, tags));
        }
        return examples;
    }

    private Step mapToStep(com.github.fridujo.glacio.ast.Step astStep, boolean background, Map<String, String> parameters) {
        Optional<Keyword> keyword;
        if (astStep instanceof RootStep) {
            com.github.fridujo.glacio.ast.Keyword astKeyword = ((RootStep) astStep).getKeyword();
            keyword = Optional.of(new Keyword(astKeyword.getLiteral(), mapToKeywordType(astKeyword)));
        } else {
            keyword = Optional.empty();
        }
        List<Step> substeps = astStep.getSubsteps().stream().map(s -> mapToStep(s, background, parameters)).collect(Collectors.toList());
        String text = resolveVariables(astStep.getText(), parameters);
        Optional<StepArgument> argument;
        if (astStep.getDataTable().isPresent()) {
            List<DataTable.Row> rows = astStep
                .getDataTable()
                .get()
                .getRows()
                .stream()
                .map(astRow -> new DataTable.Row(
                    astRow
                        .getCells()
                        .stream()
                        .map(tc -> resolveVariables(tc.getValue(), parameters))
                        .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
            argument = Optional.of(new DataTable(rows));
        } else if (astStep.getDocString().isPresent()) {
            com.github.fridujo.glacio.ast.DocString docString = astStep.getDocString().get();
            Optional<String> contentType = docString.getContentType().map(ct -> resolveVariables(ct, parameters));
            String content = resolveVariables(docString.getContent(), parameters);
            argument = Optional.of(new DocString(contentType, content));
        } else {
            argument = Optional.empty();
        }
        return new Step(background, keyword, text, argument, substeps);
    }

    private Keyword.Type mapToKeywordType(com.github.fridujo.glacio.ast.Keyword astKeyword) {
        Keyword.Type type;
        if (astKeyword.getType() == KeywordType.GIVEN) {
            type = Keyword.Type.GIVEN;
        } else if (astKeyword.getType() == KeywordType.WHEN) {
            type = Keyword.Type.WHEN;
        } else if (astKeyword.getType() == KeywordType.THEN) {
            type = Keyword.Type.THEN;
        } else {
            throw new IllegalArgumentException("Cannot map given AST keyword to model one: " + astKeyword);
        }
        return type;
    }

    private Map<String, String> buildParameters(TableRow header, TableRow parameterValues) {
        Map<String, String> parameters = new LinkedHashMap<>();
        for (int col = 0; col < header.getCells().size(); col++) {
            String varName = header.getCells().get(col).getValue();
            String varValue = parameterValues.getCells().get(col).getValue();
            parameters.put(varName, varValue);
        }
        return parameters;
    }

    private String resolveVariables(String text, Map<String, String> executionParameters) {
        if (executionParameters.isEmpty()) {
            return text;
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = GLACIO_PLACEHOLDERS_PATTERN.matcher(text);
        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = Optional.ofNullable(executionParameters.get(varName))
                .orElseGet(() -> String.format(GLACIO_PLACEHOLDER_TEMPLATE, varName));
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private Set<String> mapTags(Collection<Tag> tags) {
        return tags.stream().map(t -> t.getName()).collect(Collectors.toSet());
    }
}
