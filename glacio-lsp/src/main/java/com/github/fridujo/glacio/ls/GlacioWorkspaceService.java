package com.github.fridujo.glacio.ls;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.WorkspaceSymbolParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class GlacioWorkspaceService implements WorkspaceService {
    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        System.out.println("didChangeConfiguration");
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        System.out.println("didChangeWatchedFiles");
    }

    @Override
    public CompletableFuture<Object> executeCommand(ExecuteCommandParams params) {
        System.out.println("executeCommand");
        return null;
    }

    @Override
    public CompletableFuture<List<? extends SymbolInformation>> symbol(WorkspaceSymbolParams params) {
        System.out.println("symbol");
        return null;
    }

    @Override
    public void didChangeWorkspaceFolders(DidChangeWorkspaceFoldersParams params) {
        System.out.println("didChangeWorkspaceFolders");
    }
}
