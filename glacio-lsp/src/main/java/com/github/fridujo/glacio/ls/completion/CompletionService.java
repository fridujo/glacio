package com.github.fridujo.glacio.ls.completion;

import static com.github.fridujo.glacio.ls.completion.CompletionItemFactory.create;
import static com.github.fridujo.glacio.ls.tools.Streams.combine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import com.github.fridujo.glacio.ast.Feature;
import com.github.fridujo.glacio.ls.LanguagesProvider;
import com.github.fridujo.glacio.ls.storage.FileCache;
import com.github.fridujo.glacio.parsing.charstream.CharStream;
import com.github.fridujo.glacio.parsing.i18n.LanguageNotFoundException;
import com.github.fridujo.glacio.parsing.lexer.Lexer;
import com.github.fridujo.glacio.parsing.lexer.TokenType;
import com.github.fridujo.glacio.parsing.parser.AstParser;
import com.github.fridujo.glacio.parsing.parser.MissingTokenException;

public class CompletionService {

    private final FileCache fileCache;
    private final LanguagesProvider languagesProvider;

    public CompletionService(FileCache fileCache, LanguagesProvider languagesProvider) {
        this.fileCache = fileCache;
        this.languagesProvider = languagesProvider;
    }

    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams completionParams) {
        String rawContent = fileCache.get(completionParams.getTextDocument());
        CharStream charStream = new CharStream(rawContent);
        Lexer lexer = new Lexer(charStream);
        AstParser parser = new AstParser(lexer, languagesProvider.get());

        return CompletableFuture.completedFuture(Either.forLeft(completion(parser)));
    }

    private List<CompletionItem> completion(AstParser parser) {
        try {
            Feature feature = parser.parseFeature();
        } catch (MissingTokenException e) {
            if (e.getExpectedTokens().stream().anyMatch(t -> t.getType() == TokenType.FEATURE)) {
                return combine(
                    languagesProvider.get().defaultLanguage().getFeature().stream().map(l -> create(CompletionItemKind.Keyword, l + ": ", "Feature keyword", 1)),
                    create(CompletionItemKind.Text, "# language: ", "Explicit language declaration", 1)
                );
            }
        } catch (LanguageNotFoundException e) {
            return Collections.emptyList();
        }

        return Arrays.asList(
            create(CompletionItemKind.Keyword, "And", "Additional Given clause", 1),
            create(CompletionItemKind.Keyword, "When", "Triggering factor", 2),
            create(CompletionItemKind.Keyword, "Then", "Assertion clause", 3)
        );
    }
}
