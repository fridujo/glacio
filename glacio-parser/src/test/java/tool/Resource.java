package tool;

import com.github.fridujo.glacio.parsing.model.StringSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Resource {

    public static StringSource load(String resourceName) {
        try {
            URI uri = ClassLoader.getSystemResource(resourceName).toURI();
            String content = new String(Files.readAllBytes(Paths.get(uri)));
            return new SimpleStringSource(uri, content);
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException("Cannot read resource: " + resourceName, e);
        }
    }

    private static class SimpleStringSource implements StringSource {
        private final URI uri;
        private final String content;

        private SimpleStringSource(URI uri, String content) {
            this.uri = uri;
            this.content = content;
        }

        @Override
        public String getContent() throws UncheckedIOException {
            return content;
        }

        @Override
        public URI getURI() {
            return uri;
        }
    }
}
