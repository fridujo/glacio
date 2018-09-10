package com.github.fridujo.glacio.running.runtime.feature;

import com.github.fridujo.glacio.model.Feature;
import com.github.fridujo.glacio.parsing.ParsingException;
import com.github.fridujo.glacio.parsing.i18n.GherkinLanguages;
import com.github.fridujo.glacio.parsing.parser.ModelParser;
import com.github.fridujo.glacio.running.runtime.GlacioRunnerInitializationException;
import com.github.fridujo.glacio.running.runtime.io.GlacioIOException;
import com.github.fridujo.glacio.running.runtime.io.Resource;
import com.github.fridujo.glacio.running.runtime.io.ResourceLoader;

import java.io.UncheckedIOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FileFeatureLoader implements FeatureLoader {

    private final ResourceLoader resourceLoader;
    private final ModelParser modelParser;

    public FileFeatureLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        modelParser = new ModelParser(GherkinLanguages.load());
    }

    public Set<Feature> load(Set<String> paths) {
        return paths
            .stream()
            .map(path -> resourceLoader.resources(path, ".feature"))
            .flatMap(iter -> StreamSupport.stream(iter.spliterator(), false))
            .map(this::parse)
            .collect(Collectors.toSet());
    }

    private Feature parse(Resource resource) throws GlacioRunnerInitializationException, GlacioIOException {
        try {
            return modelParser.parse(resource);
        } catch (ParsingException e) {
            throw new GlacioRunnerInitializationException("Cannot parse feature file: " + resource.getAbsolutePath(), e);
        } catch (UncheckedIOException e) {
            throw new GlacioIOException("Cannot read from feature file: " + resource.getAbsolutePath(), e);
        }
    }
}
