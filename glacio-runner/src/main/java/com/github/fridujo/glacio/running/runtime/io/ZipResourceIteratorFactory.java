package com.github.fridujo.glacio.running.runtime.io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import static com.github.fridujo.glacio.running.runtime.io.PathHelpers.filePath;

/**
 * Factory which creates {@link ZipResourceIterator}s for URL's with "jar", "zip" and "wsjar"
 * protocols.
 */
public class ZipResourceIteratorFactory implements ResourceIteratorFactory {

    static String jarFilePath(URL jarUrl) {
        String urlFile = jarUrl.getFile();

        int separatorIndex = urlFile.indexOf("!/");
        if (separatorIndex == -1) {
            throw new GlacioIOException("Expected a jar URL: " + jarUrl.toExternalForm());
        }
        try {
            URL fileUrl = new URL(urlFile.substring(0, separatorIndex));
            return filePath(fileUrl);
        } catch (MalformedURLException e) {
            throw new GlacioIOException(e);
        }
    }

    @Override
    public boolean isFactoryFor(URL url) {
        return url.getFile().contains("!/");
    }

    @Override
    public Iterator<Resource> createIterator(URL url, String path, String suffix) {
        try {
            String jarPath = jarFilePath(url);
            return new ZipResourceIterator(jarPath, path, suffix, url.toURI());
        } catch (IOException | URISyntaxException e) {
            throw new GlacioIOException(e);
        }
    }
}
