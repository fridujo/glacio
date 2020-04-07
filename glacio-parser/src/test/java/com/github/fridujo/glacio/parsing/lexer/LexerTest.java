package com.github.fridujo.glacio.parsing.lexer;

import static com.github.fridujo.glacio.parsing.lexer.TokenType.AND;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.BACKGROUND;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.DOC_STRING_DELIMITER;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.EXAMPLES;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.FEATURE;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.GIVEN;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.SCENARIO;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.SCENARIO_OUTLINE;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.TABLE_DELIMITER;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.TEXT;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.THEN;
import static com.github.fridujo.glacio.parsing.lexer.TokenType.WHEN;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import tool.Resource;

import com.github.fridujo.glacio.parsing.charstream.CharStream;
import com.github.fridujo.glacio.parsing.charstream.Position;
import com.github.fridujo.glacio.parsing.i18n.LanguageKeywords;

public class LexerTest {
    private static final Position UNUSED_POSITION = new Position(-1, -1);
    private static final Token COMMENT_DELIMITER = token(TokenType.COMMENT_DELIMITER, "#");
    private static final Token INDENT = token(TokenType.INDENT, "    ");
    private static final Token DEDENT = token(TokenType.DEDENT, "");
    private static final Token EOL = token(TokenType.EOL, "\n");
    private static final Token SPACE = token(TokenType.SPACE, " ");
    private static final Token COLON = token(TokenType.COLON, ":");
    private static final Token TAG_DELIMITER = token(TokenType.TAG_DELIMITER, "@");

    private static Token token(TokenType type, String literal) {
        return new Token(type, literal, UNUSED_POSITION);
    }

    @Test
    void nominal() {
        String text = Resource.load("lexing_sample.txt").getContent();
        Lexer lexer = new Lexer(new CharStream(text));

        lexer.setLanguageKeywords(new LanguageKeywords(
            "test",
            "test",
            "test",
            singleton("Feature"),
            singleton("Background"),
            singleton("Scenario Outline"),
            singleton("Scenario"),
            singleton("Given "),
            singleton("When "),
            singleton("Then "),
            singleton("And "),
            singleton("Examples")));

        List<Token> tokens = listTokens(lexer);

        assertThat(tokens).containsExactly(
            COMMENT_DELIMITER, SPACE, text("comment"), EOL,
            EOL,
            token(FEATURE, "Feature"), COLON, SPACE, text("color"), EOL,
            EOL,
            INDENT, token(BACKGROUND, "Background"), COLON, EOL,
            INDENT, token(GIVEN, "Given "), text("a"), SPACE, text("color"), EOL,
            DEDENT, DEDENT, EOL,
            INDENT, TAG_DELIMITER, text("tag"), EOL,
            token(SCENARIO, "Scenario"), COLON, SPACE, text("colors"), EOL,
            INDENT, text("Description"), SPACE, text("here"), EOL,
            DEDENT, DEDENT, EOL,
            token(TokenType.INDENT, "        "), token(GIVEN, "Given "), text("yellow"), EOL,
            token(WHEN, "When "), text("blue"), EOL,
            token(THEN, "Then "), text("green"), EOL,
            token(AND, "And "), text("another"), EOL,
            DEDENT, EOL,
            INDENT, token(SCENARIO_OUTLINE, "Scenario Outline"), COLON, SPACE, text("another"), EOL,
            token(EXAMPLES, "Examples"), EOL,
            token(DOC_STRING_DELIMITER, "\"\"\""), SPACE, token(TABLE_DELIMITER, "|"), EOL,
            DEDENT);
    }

    private List<Token> listTokens(Lexer lexer) {
        List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = lexer.next()).getType() != TokenType.EOF) {
            tokens.add(token);
        }
        return tokens;
    }

    @Test
    void peek_is_idempotent() {
        Lexer lexer = new Lexer(new CharStream(": t"));

        assertThat(lexer.peek()).isEqualTo(lexer.peek());
    }

    @Test
    void peekNextNonBlankToken_returns_the_next_non_blank_token() {
        Lexer lexer = new Lexer(new CharStream(" t o"));
        assertThat(lexer.peekNextNonBlankToken()).isEqualTo(text("t"));
        lexer.skipBlanks();
        lexer.next(); // consume 't'
        assertThat(lexer.peekNextNonBlankToken()).isEqualTo(text("o"));
    }

    @Test
    void skipBlanksAndEOL_nominal() {
        Lexer lexer = new Lexer(new CharStream("t \n o"));
        assertThat(lexer.peekNextNonBlankToken()).isEqualTo(text("t"));
        lexer.consumeUntil(TokenType.SPACE);
        lexer.skipBlanksAndEOL();
        assertThat(lexer.peekNextNonBlankToken()).isEqualTo(text("o"));
    }

    @Test
    void consumeUntilNextLine_consume_EOL() {
        Lexer lexer = new Lexer(new CharStream("t \n o"));
        assertThat(lexer.consumeUntilNextLine()).isEqualTo(new TokenSequence(UNUSED_POSITION, Arrays.asList(text("t"), SPACE)));
        assertThat(lexer.consumeUntilNextLine()).isEqualTo(new TokenSequence(UNUSED_POSITION, Arrays.asList(token(TokenType.INDENT, " "), text("o"), DEDENT)));
    }

    @Test
    void broken_indentation_produces_dedents_and_one_indent_to_match_last_recorded_indentation() {
        Lexer lexer = new Lexer(new CharStream(
            "t\n"
                + "  u\n"
                + "    v\n"
                + "   w"
        ));

        List<Token> tokens = listTokens(lexer);
        assertThat(tokens).containsExactly(
            text("t"), EOL,
            token(TokenType.INDENT, "  "), text("u"), EOL,
            token(TokenType.INDENT, "  "), text("v"), EOL,
            DEDENT, token(TokenType.INDENT, " "), text("w"),
            DEDENT, DEDENT
        );
    }

    private Token text(String text) {
        return token(TEXT, text);
    }
}
