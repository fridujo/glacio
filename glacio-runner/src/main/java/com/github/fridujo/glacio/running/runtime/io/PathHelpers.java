package com.github.fridujo.glacio.running.runtime.io;

import java.net.URISyntaxException;
import java.net.URL;

public class PathHelpers {
    private PathHelpers() {
    }

    static boolean hasSuffix(String suffix, String name) {
        return suffix == null || name.endsWith(suffix);
    }

    static String filePath(URL fileUrl) throws GlacioIOException {
        if (!"file".equals(fileUrl.getProtocol())) {
            throw new GlacioIOException("Expected a file URL: " + fileUrl.toExternalForm());
        }
        try {
            return fileUrl.toURI().getSchemeSpecificPart();
        } catch (URISyntaxException e) {
            throw new GlacioIOException(e);
        }
    }
}
