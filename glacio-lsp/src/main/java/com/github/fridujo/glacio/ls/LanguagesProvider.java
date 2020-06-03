package com.github.fridujo.glacio.ls;

import com.github.fridujo.glacio.parsing.i18n.GherkinLanguages;
import com.github.fridujo.glacio.parsing.i18n.Languages;

public class LanguagesProvider {

    private Languages languages = GherkinLanguages.load();

    public Languages get() {
        return languages;
    }
}
