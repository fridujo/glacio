package com.github.fridujo.glacio.parsing.i18n;

import java.util.Set;

import com.github.fridujo.glacio.parsing.ParsingException;
import com.github.fridujo.glacio.parsing.charstream.Position;

public class LanguageNotFoundException extends ParsingException {
    public LanguageNotFoundException(Position position, String missingLanguage, Set<String> availableLanguages) {
        super(position, "Cannot find language " + missingLanguage + ", available ones are: " + availableLanguages);
    }
}
