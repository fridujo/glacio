package com.github.fridujo.glacio.ast;

import java.util.Optional;

public class DocString implements Positioned, Visitable {
    private final Position startPosition;
    private final Position endPosition;
    private final Optional<String> contentType;
    private final String content;

    public DocString(Position startPosition, Position endPosition, Optional<String> contentType, String content) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.contentType = contentType;
        this.content = content;
    }

    @Override
    public Position startPosition() {
        return startPosition;
    }

    @Override
    public Position endPosition() {
        return endPosition;
    }

    public Optional<String> getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDocString(this);
    }
}
