package tool;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Resource {

    public static String toString(String resourceName) {
        try {
            return new String(Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(resourceName).toURI())));
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException("Cannot read resource: " + resourceName, e);
        }
    }
}
