package com.github.fridujo.glacio.ls.completion;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;

public class CompletionItemFactory {
    public static CompletionItem create(CompletionItemKind kind, String label, String detail, int sortIndex) {
        CompletionItem item = new CompletionItem();
        item.setKind(kind);
        item.setLabel(label);
        item.setDetail("\u00A0" + detail);
        item.setSortText(String.valueOf(sortIndex));
        item.setDocumentation("lol");
        return item;
    }
}
