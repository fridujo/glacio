package com.github.fridujo.glacio.junit.engine;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonMap;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.junit.platform.engine.UniqueId;

import com.github.fridujo.glacio.model.Example;
import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.model.Step;

public class ModelBuilder {

    public static UniqueId uniqueId() {
        return UniqueId.forEngine(UUID.randomUUID().toString());
    }

    public static Feature feature() {
        return new Feature(URI.create("file:test"), "test", emptyList());
    }

    public static Example example() {
        return new Example(
            "test",
            singletonMap("k", "v"),
            emptyList(),
            emptySet()
        );
    }

    public static Step step() {
        return new Step(false, Optional.empty(), "test", Optional.empty(), emptyList());
    }
}
