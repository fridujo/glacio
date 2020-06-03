package com.github.fridujo.glacio.ls.storage;

import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;

public interface FileCache {

    void put(DidChangeTextDocumentParams params);

    void remove(DidCloseTextDocumentParams params);

    void remove(DidSaveTextDocumentParams params);

    String get(TextDocumentIdentifier textDocument);

    void put(DidOpenTextDocumentParams params);
}
