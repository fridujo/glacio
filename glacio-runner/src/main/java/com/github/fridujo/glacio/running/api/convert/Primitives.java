package com.github.fridujo.glacio.running.api.convert;

import java.util.LinkedHashMap;
import java.util.Map;

final class Primitives {
    private static final Map<Class<?>, Class<?>> WRAPPERS_BY_PRIMITIVE = new LinkedHashMap<>();

    static {
        WRAPPERS_BY_PRIMITIVE.put(Boolean.TYPE, Boolean.class);
        WRAPPERS_BY_PRIMITIVE.put(Character.TYPE, Character.class);
        WRAPPERS_BY_PRIMITIVE.put(Byte.TYPE, Byte.class);
        WRAPPERS_BY_PRIMITIVE.put(Short.TYPE, Short.class);
        WRAPPERS_BY_PRIMITIVE.put(Integer.TYPE, Integer.class);
        WRAPPERS_BY_PRIMITIVE.put(Long.TYPE, Long.class);
        WRAPPERS_BY_PRIMITIVE.put(Float.TYPE, Float.class);
        WRAPPERS_BY_PRIMITIVE.put(Double.TYPE, Double.class);
    }

    static Class<?> wrapperOf(Class<?> clazz) {
        return WRAPPERS_BY_PRIMITIVE.getOrDefault(clazz, clazz);
    }
}
