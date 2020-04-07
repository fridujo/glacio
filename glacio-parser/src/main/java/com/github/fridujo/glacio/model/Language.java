package com.github.fridujo.glacio.model;

public class Language {
    private final String code;
    private final String name;
    private final String nativeName;

    public Language(String code, String name, String nativeName) {
        this.code = code;
        this.name = name;
        this.nativeName = nativeName;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getNativeName() {
        return nativeName;
    }
}
