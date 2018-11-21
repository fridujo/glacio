package com.github.fridujo.glacio.parsing.lexer;

import static com.github.fridujo.glacio.parsing.tool.Strings.repeat;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

import com.github.fridujo.glacio.parsing.charstream.CharStream;
import com.github.fridujo.glacio.parsing.charstream.Position;
import com.github.fridujo.glacio.parsing.i18n.LanguageKeywords;

// TODO make the next function use greedy-match as ANTLR does to avoid matching prematurely
// when there is overlapping keywords (like 'Scenario' and 'Scenario Outline')
public class Lexer {

    public static final Predicate<Character> IS_EOL = c -> c == '\n';
    public static final Predicate<Character> IS_SPACE = c -> " \t".indexOf(c) >= 0;
    public static final Predicate<Character> IS_PIPE = c -> c == '|';
    public static final Predicate<Character> IS_COLON = c -> c == ':';
    public static final Predicate<Character> IS_NOT_A_SPACE = IS_SPACE.negate();
    public static final Predicate<Character> IS_NOT_A_DOUBLE_QUOTE = c -> c != '"';

    private final Deque<Integer> indentations = new ArrayDeque<>();
    private final Queue<Token> bufferedTokens = new LinkedList<>();
    private final CharStream charStream;

    private LanguageKeywords languageKeywords = LanguageKeywords.empty();

    public Lexer(CharStream charStream) {
        this.charStream = charStream;
        indentations.push(0);
    }

    public LanguageKeywords getLanguageKeywords() {
        return languageKeywords;
    }

    public void setLanguageKeywords(LanguageKeywords languageKeywords) {
        this.languageKeywords = languageKeywords;
    }

    public Token peek() {
        if (!bufferedTokens.isEmpty()) {
            return bufferedTokens.peek();
        } else {
            Token next = next();
            bufferedTokens.offer(next);
            return next;
        }
    }

    public Token next() {
        Position position = charStream.getPosition();
        if (charStream.getPosition().getColumn() == 0) {
            String indentationCharacters = charStream.nextUntil(IS_NOT_A_SPACE);
            computeIndentation(indentationCharacters, position);
        }
        if (!bufferedTokens.isEmpty()) {
            return bufferedTokens.poll();
        }
        String spaces = charStream.nextUntil(IS_NOT_A_SPACE);


        final Token nextToken;
        if (charStream.isEndReached()) {
            if (indentations.size() > 1) {
                indentations.pop();
                nextToken = new Token(TokenType.DEDENT, position);
            } else {
                nextToken = new Token(TokenType.EOF, position);
            }
        } else if (spaces.length() > 0) {
            nextToken = new Token(TokenType.SPACE, spaces, position);
        } else if (charStream.peek() == FixedTokenDefinition.EOL.getSingleCharacter()) {
            charStream.next();
            nextToken = new Token(FixedTokenDefinition.EOL, position);
        } else if (charStream.peek() == FixedTokenDefinition.COLON.getSingleCharacter()) {
            charStream.next();
            nextToken = new Token(FixedTokenDefinition.COLON, position);
        } else if (charStream.peek() == FixedTokenDefinition.TAG_DELIMITER.getSingleCharacter()) {
            charStream.next();
            nextToken = new Token(FixedTokenDefinition.TAG_DELIMITER, position);
        } else if (charStream.peek() == FixedTokenDefinition.COMMENT_DELIMITER.getSingleCharacter()) {
            charStream.next();
            nextToken = new Token(FixedTokenDefinition.COMMENT_DELIMITER, position);
        } else if (charStream.peek() == FixedTokenDefinition.TABLE_DELIMITER.getSingleCharacter()) {
            charStream.next();
            nextToken = new Token(FixedTokenDefinition.TABLE_DELIMITER, position);
        } else if (charStream.peekUntil(IS_NOT_A_DOUBLE_QUOTE).equals(FixedTokenDefinition.DOC_STRING_DELIMITER.getLiteralString())) {
            charStream.nextUntil(IS_NOT_A_DOUBLE_QUOTE);
            nextToken = new Token(FixedTokenDefinition.DOC_STRING_DELIMITER, position);
        } else if (charStream.anyStringAhead(languageKeywords.getFeature())) {
            String literal = charStream.nextMatchingString(languageKeywords.getFeature());
            nextToken = new Token(TokenType.FEATURE, literal, position);
        } else if (charStream.anyStringAhead(languageKeywords.getBackground())) {
            String literal = charStream.nextMatchingString(languageKeywords.getBackground());
            nextToken = new Token(TokenType.BACKGROUND, literal, position);
        } else if (charStream.anyStringAhead(languageKeywords.getScenarioOutline())) {
            String literal = charStream.nextMatchingString(languageKeywords.getScenarioOutline());
            nextToken = new Token(TokenType.SCENARIO_OUTLINE, literal, position);
        } else if (charStream.anyStringAhead(languageKeywords.getExamples())) {
            String literal = charStream.nextMatchingString(languageKeywords.getExamples());
            nextToken = new Token(TokenType.EXAMPLES, literal, position);
        } else if (charStream.anyStringAhead(languageKeywords.getScenario())) {
            String literal = charStream.nextMatchingString(languageKeywords.getScenario());
            nextToken = new Token(TokenType.SCENARIO, literal, position);
        } else if (charStream.anyStringAhead(languageKeywords.getGiven())) {
            String literal = charStream.nextMatchingString(languageKeywords.getGiven());
            nextToken = new Token(TokenType.GIVEN, literal, position);
        } else if (charStream.anyStringAhead(languageKeywords.getWhen())) {
            String literal = charStream.nextMatchingString(languageKeywords.getWhen());
            nextToken = new Token(TokenType.WHEN, literal, position);
        } else if (charStream.anyStringAhead(languageKeywords.getThen())) {
            String literal = charStream.nextMatchingString(languageKeywords.getThen());
            nextToken = new Token(TokenType.THEN, literal, position);
        } else if (charStream.anyStringAhead(languageKeywords.getAnd())) {
            String literal = charStream.nextMatchingString(languageKeywords.getAnd());
            nextToken = new Token(TokenType.AND, literal, position);
        } else {
            String literal = charStream.nextUntil(IS_EOL.or(IS_SPACE).or(IS_PIPE).or(IS_COLON));
            nextToken = new Token(TokenType.TEXT, literal, position);
        }

        return nextToken;
    }

    public Token peekNextNonBlankToken() {
        List<Token> blankTokensAccumulator = new ArrayList<>();
        Token token;
        while ((token = next()).isOfAnyType(TokenType.SPACE, TokenType.EOL, TokenType.INDENT, TokenType.DEDENT)) {
            blankTokensAccumulator.add(token);
        }
        blankTokensAccumulator.add(token);
        blankTokensAccumulator.forEach(bufferedTokens::offer);
        return token;
    }

    public void skipBlanksAndEOL() {
        skipTokensOfType(TokenType.SPACE, TokenType.EOL, TokenType.INDENT, TokenType.DEDENT);
    }

    public void skipBlanks() {
        skipTokensOfType(TokenType.SPACE, TokenType.INDENT, TokenType.DEDENT);
    }

    public void skipTokensOfType(TokenType... tokenTypes) {
        Token token;
        while ((token = next()).isOfAnyType(tokenTypes)) {
            // forget these tokens
        }
        bufferedTokens.offer(token);
    }

    public TokenSequence consumeUntil(TokenType... tokenTypes) {
        List<Token> tokenList = new ArrayList<>();
        while (!peek().isOfAnyType(tokenTypes)) {
            tokenList.add(next());
        }
        return new TokenSequence(charStream.getPosition(), tokenList);
    }

    public TokenSequence consumeUntilNextLine() {
        TokenSequence tokenSequence = consumeUntil(TokenType.EOL, TokenType.EOF);
        next(); // Consume EOL if any
        return tokenSequence;
    }

    private void computeIndentation(String literal, Position position) {
        int spaceCount = countSpaces(literal);
        Integer previousIndentation = indentations.peek();
        if (previousIndentation < spaceCount) {
            indentations.push(spaceCount);
            String remainingIndent = literal.substring(previousIndentation);
            bufferedTokens.offer(new Token(TokenType.INDENT, remainingIndent, position));
        } else if (indentations.peek() != spaceCount) {
            while (indentations.peek() > spaceCount) {
                indentations.pop();
                bufferedTokens.offer(new Token(TokenType.DEDENT, position));
            }
            if (indentations.peek() < spaceCount) {
                bufferedTokens.offer(new Token(TokenType.INDENT, repeat(' ', spaceCount - indentations.peek()), position));
                indentations.push(spaceCount);
            }
        }
    }

    private int countSpaces(String spaces) {
        return spaces.length();
    }
}
