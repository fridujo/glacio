package com.github.fridujo.glacio.running.api.convert;

import java.util.LinkedHashMap;

public final class SourceSet {
    private final LinkedHashMap<Integer, Value> valuesByPosition;

    private SourceSet(LinkedHashMap<Integer, Value> valuesByPosition) {
        this.valuesByPosition = valuesByPosition;
    }

    public static SourceSet fromRaw(Object... rawParameters) {
        LinkedHashMap<Integer, Value> valuesByPosition = new LinkedHashMap<>();
        for (int i = 0; i < rawParameters.length; i++) {
            valuesByPosition.put(i, Value.present(rawParameters[i]));
        }
        return new SourceSet(valuesByPosition);
    }

    public Value atPosition(int position) {
        return valuesByPosition.getOrDefault(position, Value.absent());
    }

    @Override
    public String toString() {
        return "SourceSet " + valuesByPosition.values();
    }
}
