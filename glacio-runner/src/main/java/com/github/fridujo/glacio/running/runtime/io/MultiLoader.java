package com.github.fridujo.glacio.running.runtime.io;

public class MultiLoader implements ResourceLoader {
    public static final String CLASSPATH_SCHEME = "classpath:";

    private final ClasspathResourceLoader classpath;
    private final FileResourceLoader fs;

    public MultiLoader(ClassLoader classLoader) {
        classpath = new ClasspathResourceLoader(classLoader);
        fs = new FileResourceLoader();
    }

    private static boolean isClasspathPath(String path) {
        return path.startsWith(CLASSPATH_SCHEME);
    }

    private static String stripClasspathPrefix(String path) {
        return path.substring(CLASSPATH_SCHEME.length());
    }

    @Override
    public Iterable<Resource> resources(String path, String suffix) {
        if (isClasspathPath(path)) {
            return classpath.resources(stripClasspathPrefix(path), suffix);
        } else {
            return fs.resources(path, suffix);
        }
    }
}
