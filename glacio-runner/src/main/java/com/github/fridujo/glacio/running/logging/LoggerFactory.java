package com.github.fridujo.glacio.running.logging;

public class LoggerFactory {
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
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException unused) {
            // unused
        }
        return false;
    }
}
