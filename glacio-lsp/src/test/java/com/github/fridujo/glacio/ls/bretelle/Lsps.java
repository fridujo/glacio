package com.github.fridujo.glacio.ls.bretelle;

import org.eclipse.lsp4j.TextDocumentItem;

public class Lsps {

    public static TextDocumentItem createTextDocumentItem(String workspaceRelativePath, String content) {
        TextDocumentItem doc = new TextDocumentItem();
        doc.setUri("file://" + workspaceRelativePath);
        doc.setText(content);
        return doc;
    }
}
