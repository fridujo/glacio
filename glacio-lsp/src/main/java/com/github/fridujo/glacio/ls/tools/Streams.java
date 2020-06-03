package com.github.fridujo.glacio.ls.tools;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Streams {

    public static <T> List<T> combine(Stream<T> list, T item) {
        return Stream.concat(list, Stream.of(item)).collect(Collectors.toList());
    }
}
