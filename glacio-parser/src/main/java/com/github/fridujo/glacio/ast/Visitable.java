package com.github.fridujo.glacio.ast;

public interface Visitable {

    void accept(Visitor visitor);
}
