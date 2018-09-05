package com.github.fridujo.glacio.parsing.i18n;

import com.github.fridujo.glacio.parsing.charstream.Position;

public interface Languages {
    default LanguageKeywords defaultLanguage() throws LanguageNotFoundException {
        return get(new Position(-1, -1), "en");
    }

    LanguageKeywords get(Position hintPosition, String language) throws LanguageNotFoundException;
}
