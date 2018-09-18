package com.github.fridujo.glacio.model;

import java.util.Optional;

public class DocString implements StepArgument {

    private final Optional<String> contentType;
    private final String content;

    public DocString(Optional<String> contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public Type getType() {
        return Type.DOC_STRING;
    }

    public Optional<String> getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }
}
