package com.github.fridujo.glacio.ls;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.lsp4j.ClientInfo;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.github.fridujo.glacio.ls.bretelle.Lsp4jServerBretelle;
import com.github.fridujo.glacio.ls.bretelle.Lsps;
import com.github.fridujo.glacio.ls.completion.CompletionItemFactory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GlacioLanguageServerTest {

    private final Lsp4jServerBretelle bretelle;

    private final LanguageServer serverClient;

    GlacioLanguageServerTest() {
        bretelle = Lsp4jServerBretelle.builder(new GlacioLanguageServer())
            .build();

        serverClient = bretelle.getLocalProxyToRemoteServer();
    }

    @Test
    void initialize_gives_completion_trigger_chars() throws ExecutionException, InterruptedException, TimeoutException {
        InitializeResult initializeResult = serverClient
            .initialize(buildInitializeParams())
            .get(50, TimeUnit.MILLISECONDS);
        ;

        assertThat(initializeResult.getCapabilities().getCompletionProvider().getTriggerCharacters()).hasSize(96);
    }

    @Test
    void keyword_completion_1() throws ExecutionException, InterruptedException, TimeoutException {
        serverClient.initialize(buildInitializeParams()).get();

        TextDocumentItem document = Lsps.createTextDocumentItem("src/test/resources/sample.feature",
            "");

        serverClient.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(document));

        TextDocumentIdentifier id = new TextDocumentIdentifier(document.getUri());
        Either<List<CompletionItem>, CompletionList> completionResult = serverClient.getTextDocumentService()
            .completion(new CompletionParams(id, new Position(0, 0)))
            .get(50, TimeUnit.MILLISECONDS);

        assertThat(completionResult.isLeft()).isTrue();
        List<CompletionItem> completionItems = completionResult.getLeft();
        assertThat(completionItems)
            .containsExactly(
                CompletionItemFactory.create(CompletionItemKind.Keyword, "And ", "Additional Given clause", 1),
                CompletionItemFactory.create(CompletionItemKind.Keyword, "When ", "Triggering factor", 2),
                CompletionItemFactory.create(CompletionItemKind.Keyword, "Then ", "Assertion clause", 3)
            );
    }

    private InitializeParams buildInitializeParams() {
        InitializeParams initializeParams = new InitializeParams();
        initializeParams.setClientInfo(new ClientInfo("test", "0.0.1"));
        return initializeParams;
    }

    @Test
    @Disabled("Too much changes right now")
    void keyword_completion() throws ExecutionException, InterruptedException, TimeoutException {
        serverClient.initialize(buildInitializeParams()).get();

        TextDocumentItem document = Lsps.createTextDocumentItem("src/main/resources/sample.feature",
            "Feature: testing colors\n" +
                "    Scenario: red\n" +
                "        Given some color\n" +
                "            the color blue\n" +
                "            the color red\n" +
                "        When this color is being tested\n" +
                "        Then the tested color is red");

        serverClient.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(document));

        TextDocumentIdentifier id = new TextDocumentIdentifier(document.getUri());
        Either<List<CompletionItem>, CompletionList> completionResult = serverClient.getTextDocumentService()
            .completion(new CompletionParams(id, new Position(5, 9)))
            .get(50, TimeUnit.MILLISECONDS);

        assertThat(completionResult.isLeft()).isTrue();
        List<CompletionItem> completionItems = completionResult.getLeft();
        assertThat(completionItems)
            .containsExactly(
                CompletionItemFactory.create(CompletionItemKind.Keyword, "And ", "Additional Given clause", 1),
                CompletionItemFactory.create(CompletionItemKind.Keyword, "When ", "Triggering factor", 2),
                CompletionItemFactory.create(CompletionItemKind.Keyword, "Then ", "Assertion clause", 3)
            );
    }

    @AfterAll
    void tearDown() {
        bretelle.close();
    }
}
