package com.github.fridujo.glacio.parsing.charstream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

class CharStreamTest {

    private static final Predicate<Character> IS_SPACE = c -> " \t".indexOf(c) >= 0;
    private static final Predicate<Character> IS_EOL = c -> c == '\n';

    @Test
    void peekUntil_nominal() {
        CharStream charStream = new CharStream("some test string");

        assertThat(charStream.peekUntil(IS_SPACE)).isEqualTo("some");
        IntStream.range(0, 5).forEach(i -> charStream.next());
        assertThat(charStream.peekUntil(IS_SPACE)).isEqualTo("test");
        IntStream.range(0, 5).forEach(i -> charStream.next());
        assertThat(charStream.peekUntil(IS_SPACE)).isEqualTo("string");
        assertThat(charStream.isEndReached()).isTrue();
    }

    @Test
    void peekUntil_empty() {
        CharStream charStream = new CharStream(" ");

        assertThat(charStream.peekUntil(IS_SPACE)).isEqualTo("");
    }

    @Test
    void peek_and_next_behave_the_same_at_EOF() {
        CharStream charStream = new CharStream("");

        assertThat(charStream.getPosition()).isEqualTo(position(1, 0));
        assertThat(charStream.peek()).isNull();
        assertThat(charStream.next()).isNull();
        assertThat(charStream.getPosition()).isEqualTo(position(1, 0));
        assertThat(charStream.isEndReached()).isTrue();
    }

    @Test
    void position_notices_line_breaks() {
        CharStream charStream = new CharStream("test\n\ntest");
        assertThat(charStream.getPosition()).isEqualTo(position(1, 0));
        assertThat(charStream.nextUntil(IS_EOL)).isEqualTo("test");
        assertThat(charStream.getPosition()).isEqualTo(position(1, 4));
        assertThat(charStream.next()).isEqualTo('\n');
        assertThat(charStream.getPosition()).isEqualTo(position(2, 0));
        assertThat(charStream.next()).isEqualTo('\n');
        assertThat(charStream.getPosition()).isEqualTo(position(3, 0));
        assertThat(charStream.nextUntil(IS_EOL)).isEqualTo("test");
        assertThat(charStream.getPosition()).isEqualTo(position(3, 4));
        assertThat(charStream.isEndReached()).isTrue();
    }

    @Test
    void anyStringAhead_returns_true_if_any_string_matches_next_characters() {
        CharStream charStream = new CharStream("maybe a mouse");

        assertThat(charStream.anyStringAhead(new HashSet<>(Arrays.asList("some", "maybe a")))).isTrue();

        assertThat(charStream.anyStringAhead(Collections.singleton("not a match"))).isFalse();
    }

    @Test
    void nextMatchingString_returns_the_matching_string_when_ahead() {
        CharStream charStream = new CharStream("maybe a mouse");

        assertThat(charStream.nextMatchingString(new HashSet<>(Arrays.asList("some", "maybe a")))).isEqualTo("maybe a");

        assertThat(charStream.nextMatchingString(Collections.singleton("not a match"))).isEqualTo("");
    }

    @Test
    void position_equals_and_hashcode() {
        assertThat(position(3, 4)).isEqualTo(position(3, 4));
        assertThat(position(3, 4)).hasSameHashCodeAs(position(3, 4));

        assertThat(position(3, 4)).isNotEqualTo(position(4, 4));
        assertThat(position(3, 4).hashCode()).isNotEqualTo(position(4, 4).hashCode());
    }

    private Position position(int line, int col) {
        return new Position(line, col);
    }
}
