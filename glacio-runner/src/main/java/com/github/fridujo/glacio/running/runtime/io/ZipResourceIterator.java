package com.github.fridujo.glacio.running.runtime.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourceIterator implements Iterator<Resource> {
    private final String path;
    private final String suffix;
    private final URI uri;
    private final ZipFile jarFile;
    private final Enumeration<? extends ZipEntry> entries;
    private Resource next;

    public ZipResourceIterator(String zipPath, String path, String suffix, URI uri) throws IOException {
        this.path = path;
        this.suffix = suffix;
        this.uri = uri;
        jarFile = new ZipFile(zipPath);
        entries = jarFile.entries();

        moveToNext();
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Resource next() {
        try {
            if (next == null) {
                throw new NoSuchElementException();
            }
            return next;
        } finally {
            moveToNext();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void moveToNext() {
        next = null;
        while (entries.hasMoreElements()) {
            ZipEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.startsWith(path) && PathHelpers.hasSuffix(suffix, entryName)) {
                next = new ZipResource(jarFile, jarEntry, URI.create(uri.toString() + File.separator + entryName));
                break;
            }
        }
    }
}
