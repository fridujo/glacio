package com.github.fridujo.glacio.ls.storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;

/**
 * Cache of live file modifications.
 * If a file is saved, it will be removed from the cache, as the cache fallback on FS.
 */
class InMemoryFileCache implements FileCache {

    private final Map<String, VersionedFile> documentsByUri = new LruCacheWithFsBackup<>(15, VersionedFile.class);

    @Override
    public void put(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();
        Integer version = params.getTextDocument().getVersion();

        internal_put(uri, text, version);
    }

    public void put(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getContentChanges().get(0).getText();
        Integer version = params.getTextDocument().getVersion();

        internal_put(uri, text, version);
    }

    private void internal_put(String uri, String text, Integer version) {
        VersionedFile previousVersion = documentsByUri.get(uri);
        if (previousVersion == null) {
            documentsByUri.put(uri, new VersionedFile(version, text));
        } else {
            previousVersion.update(version, text);
        }
    }

    public void remove(DidCloseTextDocumentParams params) {
        documentsByUri.remove(params.getTextDocument().getUri());
    }

    public void remove(DidSaveTextDocumentParams params) {
        documentsByUri.remove(params.getTextDocument().getUri());
    }

    @Override
    public String get(TextDocumentIdentifier textDocumentIdentifier) {
        String uri = textDocumentIdentifier.getUri();
        VersionedFile versionedFile = documentsByUri.get(uri);
        if (versionedFile != null) {
            return versionedFile.getText();
        } else {
            try {
                String text = new String(Files.readAllBytes(Paths.get(URI.create(uri))));
                documentsByUri.put(uri, new VersionedFile(-1, text));
                return text;
            } catch (IOException e) {
                throw new UncheckedIOException("Cannot read file: " + uri, e);
            }
        }
    }
}
