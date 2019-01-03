package com.github.fridujo.glacio.running.logging;

public abstract class LoggerFactory {
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    private LoggerFactory() {
    }

    public static Logger getLogger(Class<?> origin) {
        if (isClassPresent("org.slf4j.Logger")) {
            return new Slf4jLogger(origin);
        }
        return new JulLogger(origin);
    }

    private static boolean isClassPresent(String className) {
        try {
            CLASS_LOADER.loadClass(className);
            return true;
        } catch (ClassNotFoundException unused) {
            // unused
        }
        return false;
    }
}
