package com.github.fridujo.glacio.ls.hover;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.MarkedString;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import com.github.fridujo.glacio.ast.Feature;
import com.github.fridujo.glacio.ls.LanguagesProvider;
import com.github.fridujo.glacio.ls.storage.FileCache;
import com.github.fridujo.glacio.parsing.charstream.CharStream;
import com.github.fridujo.glacio.parsing.i18n.LanguageNotFoundException;
import com.github.fridujo.glacio.parsing.lexer.Lexer;
import com.github.fridujo.glacio.parsing.parser.AstParser;
import com.github.fridujo.glacio.parsing.parser.MissingTokenException;

public class HoverService {

    private final FileCache fileCache;
    private final LanguagesProvider languagesProvider;

    public HoverService(FileCache fileCache, LanguagesProvider languagesProvider) {
        this.fileCache = fileCache;
        this.languagesProvider = languagesProvider;
    }

    public CompletableFuture<Hover> hover(HoverParams params) {
        String rawContent = fileCache.get(params.getTextDocument());
        Hover response = new Hover();
        // TODO token at pos

        // TODO a simple way to get all token until
        CharStream charStream = new CharStream(rawContent);
        Lexer lexer = new Lexer(charStream);
        AstParser parser = new AstParser(lexer, languagesProvider.get());
        try {
            Feature feature = parser.parseFeature();
            findHoverInformation(params.getPosition(), feature, response);
        } catch (MissingTokenException | LanguageNotFoundException e) {
            // eat this, response is returned without mutation   
        }
        return CompletableFuture.completedFuture(response);
    }

    private void findHoverInformation(Position position, Feature feature, Hover response) {
        NodesAtPosCollector collector = new NodesAtPosCollector(toAstPosition(position));
        feature.accept(collector);
        response.setContents(
            collector.getMatchingNodes()
                .stream()
                .map(Object::toString)
                .map(left -> Either.<String, MarkedString>forLeft(left))
                .collect(Collectors.toList())
        );
    }

    private com.github.fridujo.glacio.ast.Position toAstPosition(Position position) {
        return new com.github.fridujo.glacio.ast.Position(position.getLine() + 1, position.getCharacter());
    }
}
