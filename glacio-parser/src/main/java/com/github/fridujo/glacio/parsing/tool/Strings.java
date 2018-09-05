package com.github.fridujo.glacio.parsing.tool;

public class Strings {

    public static String repeat(char ch, int repetitions) {
        return new String(new char[repetitions]).replace('\0', ch);
    }
}
