package com.github.fridujo.glacio.ls.hover;

import java.util.ArrayList;
import java.util.List;

import com.github.fridujo.glacio.ast.Position;
import com.github.fridujo.glacio.ast.Positioned;
import com.github.fridujo.glacio.ast.Visitor;

public class NodesAtPosCollector implements Visitor {
    
    private final Position position;
    
    private final List<Positioned> matchingNodes = new ArrayList<>();

    public NodesAtPosCollector(Position position) {
        this.position = position;
    }

    public void visitPositioned(Positioned positioned) {
        if(position.isIn(positioned)) {
            matchingNodes.add(positioned);
        }
    }

    public List<Positioned> getMatchingNodes() {
        return matchingNodes;
    }
}
