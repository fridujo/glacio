package com.github.fridujo.glacio.ls;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;

import com.github.fridujo.glacio.ls.completion.CompletionService;
import com.github.fridujo.glacio.ls.hover.HoverService;
import com.github.fridujo.glacio.ls.storage.FileCache;
import com.github.fridujo.glacio.ls.storage.FileCacheFactory;

public class GlacioTextDocumentService implements TextDocumentService {

    private final FileCache fileCache = FileCacheFactory.inMemoryFileCache();
    private final LanguagesProvider languagesProvider = new LanguagesProvider();
    private final CompletionService completionService = new CompletionService(fileCache, languagesProvider);
    private final HoverService hoverService = new HoverService(fileCache, languagesProvider);

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        fileCache.put(params);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        fileCache.put(params);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        fileCache.remove(params);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        fileCache.remove(params);
    }

    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams completionParams) {
        return completionService.completion(completionParams);
    }

    public CompletableFuture<Hover> hover(HoverParams params) {
        return hoverService.hover(params);
    }
}
