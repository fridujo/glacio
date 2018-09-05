package com.github.fridujo.glacio.ast;

import java.util.Optional;

public class DocString implements Positioned {
    private final Position position;
    private final Optional<String> contentType;
    private final String content;

    public DocString(Position position, Optional<String> contentType, String content) {
        this.position = position;
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public Optional<String> getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }
}
