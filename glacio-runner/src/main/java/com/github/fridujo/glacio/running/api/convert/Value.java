package com.github.fridujo.glacio.running.api.convert;

import static com.github.fridujo.glacio.running.api.convert.Primitives.wrapperOf;

/**
 * Similar to {@link java.util.Optional} except a {@code null} value can be present.<br/>
 * <p>
 * This way a {@link Converter} is able to return either:
 * <ul>
 * <li>no value, in this case, the next {@link Converter} will be used</li>
 * <li>a value, which can be null</li>
 * </ul>
 */
public class Value {
    private static final Value ABSENT = new Value(false, null, null);
    public final Object value;
    public final boolean present;
    public final Class<?> clazz;

    private Value(boolean present, Object value, Class<?> clazz) {
        this.value = value;
        this.present = present;
        this.clazz = wrapperOf(clazz);
    }

    public static Value present(Object value) {
        return new Value(true, value, value != null ? value.getClass() : Void.class);
    }

    public static Value absent() {
        return ABSENT;
    }

    @Override
    public String toString() {
        return present ? "{" + value + '}' : "<absent>";
    }
}
