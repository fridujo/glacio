package com.github.fridujo.glacio.running.runtime.io;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

public class ClasspathResourceIterable implements Iterable<Resource> {
    private final ResourceIteratorFactory resourceIteratorFactory = new ZipThenFileResourceIteratorFactory();

    private final ClassLoader classLoader;
    private final String path;
    private final String suffix;

    public ClasspathResourceIterable(ClassLoader classLoader, String path, String suffix) {
        this.classLoader = classLoader;
        this.path = path;
        this.suffix = suffix;
    }

    @Override
    public Iterator<Resource> iterator() throws GlacioIOException {
        try {
            FlatteningIterator<Resource> iterator = new FlatteningIterator<>();
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                Iterator<Resource> resourceIterator = resourceIteratorFactory.createIterator(url, path, suffix);
                iterator.push(resourceIterator);
            }
            return iterator;
        } catch (IOException e) {
            throw new GlacioIOException(e);
        }
    }
}
