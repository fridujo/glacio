package com.github.fridujo.glacio.model;

public interface StepArgument {

    Type getType();

    enum Type {
        DOC_STRING, DATA_TABLE
    }
}
