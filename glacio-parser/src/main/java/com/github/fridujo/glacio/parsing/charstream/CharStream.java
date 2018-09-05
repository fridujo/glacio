package com.github.fridujo.glacio.parsing.charstream;

import java.util.Set;
import java.util.function.Predicate;

public class CharStream {

    private final String input;
    private int pos;
    private int line = 1;
    private int col;
    private boolean endReached;

    public CharStream(String input) {
        this.input = input
            .replace("\r", "")
            .replace("\t", "    ");
    }

    /**
     * @return null if the end of the stream is reached
     */
    public Character next() {
        Character ch = charAt(pos);
        if (ch != null) {
            updatePosition(ch);
        }
        return ch;
    }

    private void updatePosition(Character ch) {
        pos++;
        if (ch == '\n') {
            line++;
            col = 0;
        } else {
            col++;
        }
    }

    /**
     * @return null if the end of the stream is reached
     */
    public Character peek() {
        return charAt(pos);
    }

    public String peekUntil(Predicate<Character> charPredicate) {
        StringBuilder sb = new StringBuilder();
        int peekPos = pos;
        Character nextChar;
        while ((nextChar = charAt(peekPos)) != null) {
            if (charPredicate.test(nextChar)) {
                break;
            } else {
                sb.append(nextChar);
                peekPos++;
            }
        }

        return sb.toString();
    }

    public String nextUntil(Predicate<Character> charPredicate) {
        StringBuilder sb = new StringBuilder();
        int peekPos = pos;
        Character nextChar;
        while ((nextChar = charAt(peekPos)) != null) {
            if (charPredicate.test(nextChar)) {
                break;
            } else {
                peekPos++;
                sb.append(nextChar);
                updatePosition(nextChar);
            }
        }
        return sb.toString();
    }

    private Character charAt(int position) {
        if (input.length() <= position) {
            endReached = true;
            return null;
        } else {
            return input.charAt(position);
        }
    }

    public boolean isEndReached() {
        return endReached;
    }

    public boolean anyStringAhead(Set<String> strings) {
        for (String s : strings) {
            int endOfString = pos + s.length();
            if (input.length() >= endOfString && s.equals(input.substring(pos, endOfString))) {
                return true;
            }
        }
        return false;
    }

    public String nextMatchingString(Set<String> strings) {
        for (String s : strings) {
            int endOfString = pos + s.length();
            if (endOfString > input.length()) {
                continue;
            }
            String potentialMatch = input.substring(pos, endOfString);
            if (input.length() >= endOfString && s.equals(potentialMatch)) {
                pos = endOfString;
                // assuming there is no EOL in given set of Strings
                col += potentialMatch.length();
                return potentialMatch;
            }
        }
        return "";
    }

    public Position getPosition() {
        return new Position(line, col);
    }
}
