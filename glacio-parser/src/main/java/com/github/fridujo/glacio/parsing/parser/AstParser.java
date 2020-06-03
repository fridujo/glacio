package com.github.fridujo.glacio.parsing.parser;

import static com.github.fridujo.glacio.ast.Position.from;
import static com.github.fridujo.glacio.parsing.parser.DynamicTokenDefinition.dynamicToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.fridujo.glacio.ast.Background;
import com.github.fridujo.glacio.ast.DataTable;
import com.github.fridujo.glacio.ast.DocString;
import com.github.fridujo.glacio.ast.Examples;
import com.github.fridujo.glacio.ast.Feature;
import com.github.fridujo.glacio.ast.Keyword;
import com.github.fridujo.glacio.ast.KeywordType;
import com.github.fridujo.glacio.ast.Position;
import com.github.fridujo.glacio.ast.PositionedString;
import com.github.fridujo.glacio.ast.Scenario;
import com.github.fridujo.glacio.ast.ScenarioOutline;
import com.github.fridujo.glacio.ast.Step;
import com.github.fridujo.glacio.ast.TableCell;
import com.github.fridujo.glacio.ast.TableRow;
import com.github.fridujo.glacio.ast.Tag;
import com.github.fridujo.glacio.parsing.ParsingException;
import com.github.fridujo.glacio.parsing.charstream.CharStream;
import com.github.fridujo.glacio.parsing.i18n.Languages;
import com.github.fridujo.glacio.parsing.lexer.FixedTokenDefinition;
import com.github.fridujo.glacio.parsing.lexer.Lexer;
import com.github.fridujo.glacio.parsing.lexer.Token;
import com.github.fridujo.glacio.parsing.lexer.TokenSequence;
import com.github.fridujo.glacio.parsing.lexer.TokenType;

public class AstParser {

    private final Pattern languageHintPattern = Pattern.compile("^language\\s*:\\s*(?<language>[^\\s]+)$");
    private final Lexer lexer;
    private final Languages languages;

    public AstParser(Lexer lexer, Languages languages) {
        this.lexer = lexer;
        this.languages = languages;
        this.lexer.setLanguageKeywords(languages.defaultLanguage());
    }

    public Feature parseFeature() {
        Optional<PositionedString> language = consumeComments(true);
        List<Tag> tags = parseTags();
        Token potentialFeature = lexer.next();
        if (potentialFeature.isOfType(TokenType.FEATURE)) {
            lexer.skipBlanks();
            Token potentialColon = lexer.next();
            if (potentialColon.isOfType(TokenType.COLON)) {
                String name = lexer.consumeUntilNextLine().toLiteral().trim();
                Optional<String> description = parseDescription();
                Optional<Background> background = parseBackground();
                List<Scenario> scenarios = parseScenarios();
                return new Feature(from(potentialFeature.getPosition()), from(lexer.getCurrentTokenEndPosition()), name, language, tags, description, background, scenarios);
            } else {
                throw new MissingTokenException(FixedTokenDefinition.COLON, potentialColon);
            }
        } else {
            throw new MissingTokenException(potentialFeature, dynamicToken(TokenType.FEATURE, lexer.getLanguageKeywords().getFeature()));
        }
    }

    private Optional<Background> parseBackground() {
        lexer.skipBlanksAndEOL();
        Token potentialBackgroundToken = lexer.peek();
        if (potentialBackgroundToken.isOfType(TokenType.BACKGROUND)) {
            lexer.next(); // Consume BACKGROUND token
            lexer.skipBlanks();
            Token potentialColon = lexer.next();
            if (potentialColon.isOfType(TokenType.COLON)) {
                lexer.consumeUntilNextLine();
                Optional<String> description = parseDescription();
                List<Step> steps = parseRootSteps();
                return Optional.of(new Background(from(potentialBackgroundToken.getPosition()), from(lexer.getCurrentTokenEndPosition()), description, steps));
            } else {
                throw new MissingTokenException(FixedTokenDefinition.COLON, potentialColon);
            }
        } else {
            return Optional.empty();
        }
    }

    private List<Scenario> parseScenarios() {
        List<Scenario> scenarios = new ArrayList<>();
        while (!lexer.peekNextNonBlankToken().isOfType(TokenType.EOF)) {
            scenarios.add(parseScenario());
        }
        return scenarios;
    }

    public Scenario parseScenario() {
        consumeComments(false);
        List<Tag> tags = parseTags();
        Token potentialScenario = lexer.next();
        if (potentialScenario.isOfType(TokenType.SCENARIO)) {
            lexer.skipBlanks();
            Token potentialColon = lexer.next();
            if (potentialColon.isOfType(TokenType.COLON)) {
                Position startPosition = from(potentialScenario.getPosition());
                String name = lexer.consumeUntilNextLine().toLiteral().trim();
                Optional<String> description = parseDescription();
                List<Step> steps = parseRootSteps();
                return new Scenario(startPosition, from(lexer.getCurrentTokenEndPosition()), name, tags, description, steps);
            } else {
                throw new MissingTokenException(FixedTokenDefinition.COLON, potentialColon);
            }
        } else if (potentialScenario.isOfType(TokenType.SCENARIO_OUTLINE)) {
            lexer.skipBlanks();
            Token potentialColon = lexer.next();
            if (potentialColon.isOfType(TokenType.COLON)) {
                Position startPosition = from(potentialScenario.getPosition());
                String name = lexer.consumeUntilNextLine().toLiteral().trim();
                Optional<String> description = parseDescription();
                List<Step> steps = parseRootSteps();
                Examples examples = parseExamples();
                return new ScenarioOutline(startPosition, from(lexer.getCurrentTokenEndPosition()), name, tags, description, steps, examples);
            } else {
                throw new MissingTokenException(FixedTokenDefinition.COLON, potentialColon);
            }
        } else {
            throw new MissingTokenException(potentialScenario,
                dynamicToken(TokenType.SCENARIO, lexer.getLanguageKeywords().getScenario()),
                dynamicToken(TokenType.SCENARIO_OUTLINE, lexer.getLanguageKeywords().getScenarioOutline())
            );
        }
    }

    private List<Step> parseRootSteps() {
        lexer.skipBlanksAndEOL();
        consumeComments(false);

        List<Step> rootSteps = new ArrayList<>();
        KeywordType previousKeywordType = null;
        while (lexer.peek().isOfAnyType(TokenType.GIVEN, TokenType.WHEN, TokenType.THEN, TokenType.AND)) {
            Token keywordToken = lexer.next();
            Position startPosition = from(keywordToken.getPosition());
            Keyword keyword = mapKeyword(keywordToken, previousKeywordType);
            previousKeywordType = keyword.getType();
            String text = lexer.consumeUntilNextLine().toLiteral();
            Optional<DocString> docString = parseDocString();
            Optional<DataTable> dataTable = parseDataTable();
            List<Step> substeps = parseSubsteps();
            rootSteps.add(new Step(startPosition, from(lexer.getCurrentTokenEndPosition()), Optional.of(keyword), text, substeps, docString, dataTable));
            consumeComments(false);
        }

        return rootSteps;
    }

    private List<Step> parseSubsteps() {
        List<Step> steps = new ArrayList<>();
        if (lexer.peek().isOfType(TokenType.INDENT)) {
            lexer.next();
            while (!lexer.peek().isOfAnyType(TokenType.DEDENT, TokenType.EOF)) {
                Position startPosition = from(lexer.peek().getPosition());
                String text = lexer.consumeUntilNextLine().toLiteral();
                Optional<DocString> docString = parseDocString();
                Optional<DataTable> dataTable = parseDataTable();
                List<Step> substeps = parseSubsteps();
                steps.add(new Step(startPosition, from(lexer.getCurrentTokenEndPosition()), Optional.empty(), text, substeps, docString, dataTable));
            }
            lexer.next(); // Consume DEDENT
        }
        return steps;
    }

    private List<Tag> parseTags() {
        lexer.skipBlanksAndEOL();
        List<Tag> tags = new ArrayList<>();
        while (lexer.peek().isOfType(TokenType.TAG_DELIMITER)) {
            lexer.next(); // Consume TAG_DELIMITER
            TokenSequence tagName = lexer.consumeUntil(TokenType.SPACE, TokenType.TAG_DELIMITER, TokenType.EOL);
            tags.add(new Tag(tagName.toLiteral()));
            lexer.skipBlanksAndEOL();
        }
        lexer.skipBlanksAndEOL();
        return tags;
    }

    private Optional<String> parseDescription() {
        TokenSequence descriptionTokenSequence = lexer.consumeUntil(TokenType.BACKGROUND, TokenType.SCENARIO, TokenType.SCENARIO_OUTLINE, TokenType.EXAMPLES, TokenType.GIVEN, TokenType.WHEN, TokenType.THEN, TokenType.AND, TokenType.EOF);
        String unformattedDescription = descriptionTokenSequence.toLiteral();
        final Optional<String> description;
        if (unformattedDescription.trim().length() > 0) {
            description = Optional.of(formatTextBloc(unformattedDescription));
        } else {
            description = Optional.empty();
        }
        return description;
    }

    private Examples parseExamples() {
        lexer.skipBlanksAndEOL();
        Token potentialExamples = lexer.next();
        if (potentialExamples.isOfType(TokenType.EXAMPLES)) {
            lexer.skipBlanks();
            Token potentialColon = lexer.next();
            if (potentialColon.isOfType(TokenType.COLON)) {
                lexer.skipBlanksAndEOL();
                TableRow header = parseTableRow();
                List<TableRow> body = new ArrayList<>();
                while (lexer.peek().isOfType(TokenType.TABLE_DELIMITER)) {
                    body.add(parseTableRow());
                }
                return new Examples(from(potentialExamples.getPosition()), from(lexer.getCurrentTokenEndPosition()), header, body);
            } else {
                throw new MissingTokenException(FixedTokenDefinition.COLON, potentialColon);
            }
        } else {
            throw new MissingTokenException(potentialExamples, dynamicToken(TokenType.EXAMPLES, lexer.getLanguageKeywords().getExamples()));
        }
    }

    private Optional<DocString> parseDocString() {
        final Optional<DocString> docString;
        Token potentialDocStringDelimiter = lexer.peekNextNonBlankToken();
        if (potentialDocStringDelimiter.isOfType(TokenType.DOC_STRING_DELIMITER)) {
            lexer.skipBlanksAndEOL();
            lexer.next(); // consume DOC_STRING_DELIMITER
            // TODO accumulate INDENT/DEDENT Tokens to consume the same amount of DEDENT/INDENT after second delimiter
            Optional<String> contentType = optional(lexer.consumeUntilNextLine().toLiteral().trim());
            TokenSequence contentTokenSequence = lexer.consumeUntil(TokenType.DOC_STRING_DELIMITER);
            lexer.next(); // Consume second delimiter
            lexer.skipTokensOfType(TokenType.SPACE, TokenType.EOL, TokenType.INDENT);
            String content = formatTextBloc(contentTokenSequence.toLiteral());
            docString = Optional.of(new DocString(from(potentialDocStringDelimiter.getPosition()), from(lexer.getCurrentTokenEndPosition()), contentType, content));
        } else {
            docString = Optional.empty();
        }
        return docString;
    }

    private Optional<DataTable> parseDataTable() {
        final Optional<DataTable> docString;
        Token potentialFirstTableDelimiter = lexer.peekNextNonBlankToken();
        if (potentialFirstTableDelimiter.isOfType(TokenType.TABLE_DELIMITER)) {
            // TODO accumulate INDENT/DEDENT Tokens to consume the same amount of DEDENT/INDENT after second delimiter
            lexer.skipBlanksAndEOL();
            List<TableRow> rows = new ArrayList<>();
            while (lexer.peekNextNonBlankToken().isOfType(TokenType.TABLE_DELIMITER)) {
                rows.add(parseTableRow());
            }
            return Optional.of(new DataTable(from(potentialFirstTableDelimiter.getPosition()), from(lexer.getCurrentTokenEndPosition()), rows));
        } else {
            docString = Optional.empty();
        }
        return docString;
    }

    private TableRow parseTableRow() {
        Token potentialTableDelimiter = lexer.next();
        if (potentialTableDelimiter.isOfType(TokenType.TABLE_DELIMITER)) {
            List<TableCell> cells = new ArrayList<>();
            while (!lexer.peek().isOfAnyType(TokenType.EOL, TokenType.EOF)) {
                TokenSequence tokenSequence = lexer.consumeUntil(TokenType.TABLE_DELIMITER, TokenType.EOL, TokenType.EOF);
                cells.add(new TableCell(tokenSequence.toLiteral().trim()));
                Token potentialEndCellDelimiter = lexer.next();
                if (!potentialEndCellDelimiter.isOfType(TokenType.TABLE_DELIMITER)) {
                    throw new MissingTokenException(FixedTokenDefinition.TABLE_DELIMITER, potentialEndCellDelimiter);
                }
                lexer.skipBlanks();
            }
            lexer.consumeUntilNextLine();
            return new TableRow(from(potentialTableDelimiter.getPosition()), from(lexer.getCurrentTokenEndPosition()), cells);
        } else {
            throw new MissingTokenException(FixedTokenDefinition.TABLE_DELIMITER, potentialTableDelimiter);
        }
    }

    private Optional<PositionedString> consumeComments(boolean interpretLanguageHint) {
        PositionedString explicitLanguage = null;
        while (lexer.peekNextNonBlankToken().isOfType(TokenType.COMMENT_DELIMITER)) {
            lexer.skipBlanksAndEOL();
            lexer.next(); // consume COMMENT_DELIMITER
            TokenSequence tokenSequence = lexer.consumeUntilNextLine();
            String comment = tokenSequence.toLiteral().trim();
            Matcher languageMatcher = languageHintPattern.matcher(comment);
            if (interpretLanguageHint && languageMatcher.matches()) {
                String language = languageMatcher.group("language");
                Position languagePosition = from(tokenSequence.getPosition());
                lexer.setLanguageKeywords(languages.get(languagePosition, language));
                explicitLanguage = new PositionedString(languagePosition, from(lexer.getCurrentTokenEndPosition()), language);
            }
            lexer.skipBlanksAndEOL();
        }
        return Optional.ofNullable(explicitLanguage);
    }

    private String formatTextBloc(String unformattedText) {
        String trimmedOfFirstEmptyLines = trimLeftEmptyLines(unformattedText);
        String firstIndent = new CharStream(trimmedOfFirstEmptyLines).peekUntil(Lexer.IS_NOT_A_SPACE);
        String[] lines = trimmedOfFirstEmptyLines.split("\n");
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            if (lines[lineIndex].startsWith(firstIndent)) {
                lines[lineIndex] = lines[lineIndex].substring(firstIndent.length());
            }
        }
        return Arrays.stream(lines).collect(Collectors.joining("\n")).trim();
    }

    private String trimLeftEmptyLines(String text) {
        String[] lines = text.split("\n");
        int firstNonBlankLineIndex = -1;
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            if (lines[lineIndex].trim().length() > 0) {
                firstNonBlankLineIndex = lineIndex;
                break;
            }
        }
        String[] linesWithFirstOneNonEmpty = Arrays.copyOfRange(lines, firstNonBlankLineIndex, lines.length);
        return Arrays.stream(linesWithFirstOneNonEmpty).collect(Collectors.joining("\n"));
    }

    private Keyword mapKeyword(Token keywordToken, KeywordType previousKeywordType) {
        final KeywordType keywordType;
        if (keywordToken.isOfType(TokenType.AND)) {
            if (previousKeywordType == null) {
                throw new ParsingException(keywordToken.getPosition(), "Found AND keyword before any GIVEN, WHEN or THEN one");
            } else {
                keywordType = previousKeywordType;
            }
        } else if (keywordToken.isOfType(TokenType.GIVEN)) {
            keywordType = KeywordType.GIVEN;
        } else if (keywordToken.isOfType(TokenType.WHEN)) {
            keywordType = KeywordType.WHEN;
        } else if (keywordToken.isOfType(TokenType.THEN)) {
            keywordType = KeywordType.THEN;
        } else {
            throw new IllegalStateException("Not a Keyword token");
        }

        return new Keyword(keywordType, keywordToken.getLiteral());
    }

    private Optional<String> optional(String s) {
        final Optional<String> os;
        if (s == null || s.trim().length() == 0) {
            os = Optional.empty();
        } else {
            os = Optional.of(s.trim());
        }
        return os;
    }
}
