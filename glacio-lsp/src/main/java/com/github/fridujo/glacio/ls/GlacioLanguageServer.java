package com.github.fridujo.glacio.ls;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.InitializedParams;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.ServerInfo;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.jsonrpc.json.StreamMessageProducer;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import com.github.fridujo.glacio.ls.tools.FileLogger;

public class GlacioLanguageServer implements LanguageServer, LanguageClientAware {

    private final TextDocumentService textDocumentService = new GlacioTextDocumentService();
    private final WorkspaceService workspaceService = new GlacioWorkspaceService();
    private LanguageClient client;
    private StreamMessageProducer messageProducer;

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
        System.out.println("Connection from: " + initializeParams.getClientInfo().getName() + " (v." + initializeParams.getClientInfo().getVersion() + ")");
        final InitializeResult res = new InitializeResult(new ServerCapabilities(), new ServerInfo());
        res.getServerInfo().setName("Glacio");
        // Load that from M2 metadata
        res.getServerInfo().setVersion("0.0.1-SNAPSHOT");

        res.getCapabilities().setTextDocumentSync(TextDocumentSyncKind.Full);
        res.getCapabilities().setCompletionProvider(new CompletionOptions(false,
            Stream.concat(
                IntStream.range(32, 127).mapToObj(i -> Character.toString((char) i)),
                Stream.of("")
            ).collect(Collectors.toList())
        ));
        //res.getCapabilities().setCodeActionProvider(new CodeActionOptions());
        //res.getCapabilities().setDefinitionProvider(Boolean.TRUE);
        res.getCapabilities().setHoverProvider(Boolean.TRUE);
        //res.getCapabilities().setReferencesProvider(Boolean.TRUE);
        //res.getCapabilities().setDocumentSymbolProvider(Boolean.TRUE);
        //res.getCapabilities().setCodeLensProvider(new CodeLensOptions(true));

        return CompletableFuture.completedFuture(res);
    }

    @Override
    public void initialized(InitializedParams params) {
        if (client != null) {
            client.logMessage(new MessageParams(MessageType.Info, "Connected to Glacio LS."));
        }
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.supplyAsync(() -> Boolean.TRUE);
    }

    @Override
    public void exit() {
        if (messageProducer != null) {
            messageProducer.close();
        }
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
    }

    public void setMessageProducer(StreamMessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }
}
