package com.github.fridujo.glacio.ls.storage;

public class VersionedFile {

    private int version;
    private String text;

    public VersionedFile(int version, String text) {
        this.version = version;
        this.text = text;
    }

    public void update(int newVersion, String text) {
        if (newVersion > version) {
            this.version = newVersion;
            this.text = text;
        }
    }

    public int getVersion() {
        return version;
    }

    public String getText() {
        return text;
    }
}
