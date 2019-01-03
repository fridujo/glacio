package com.github.fridujo.glacio.junit.engine.discovery;

import static java.util.stream.Stream.concat;
import static org.junit.platform.commons.util.ReflectionUtils.findAllClassesInClasspathRoot;
import static org.junit.platform.commons.util.ReflectionUtils.findAllClassesInModule;
import static org.junit.platform.commons.util.ReflectionUtils.findAllClassesInPackage;
import static org.junit.platform.engine.support.filter.ClasspathScanningSupport.buildClassFilter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.platform.commons.util.ClassFilter;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.ModuleSelector;
import org.junit.platform.engine.discovery.PackageSelector;

import com.github.fridujo.glacio.junit.engine.GlacioProperties;
import com.github.fridujo.glacio.junit.engine.descriptor.GlacioConfigurationDescriptor;
import com.github.fridujo.glacio.running.logging.Logger;
import com.github.fridujo.glacio.running.logging.LoggerFactory;
import com.github.fridujo.glacio.running.runtime.configuration.ConfigurationContextBuilder;

public class DiscoverySelectorResolver {

    private static final Logger logger = LoggerFactory.getLogger(DiscoverySelectorResolver.class);
    private static final Predicate<Class<?>> isGlacioInitializerClass = new IsGlacioConfigurationClass();

    private final ConfigurationContextBuilder configurationContextBuilder = new ConfigurationContextBuilder();
    private final boolean disableEngine;
    private final EngineDiscoveryRequest request;
    private final ClassFilter classFilter;

    public DiscoverySelectorResolver(EngineDiscoveryRequest request) {
        disableEngine = request
            .getConfigurationParameters()
            .get(GlacioProperties.DISABLED_PROPERTY_NAME)
            .filter("true"::equals)
            .isPresent();
        this.request = request;
        this.classFilter = buildClassFilter(request, isGlacioInitializerClass);
    }

    @SuppressWarnings("unchecked")
    public void resolveFor(TestDescriptor testDescriptor) {
        if (disableEngine) {
            logger.info("Glacio disabled");
            return;
        }
        Set<Class<?>> configurationClasses = findConfigurationClasses();

        configurationClasses
            .stream()
            .map(configurationContextBuilder::fromClass)
            .map(config -> new GlacioConfigurationDescriptor(testDescriptor.getUniqueId(), config))
            .forEach(testDescriptor::addChild);
    }

    private LinkedHashSet<Class<?>> findConfigurationClasses() {
        return concat(
            classesFromClasspathRootSelectors(),
            concat(
                classesFromModuleSelectors(),
                concat(classesFromPackageSelectors(),
                    classesFromClassSelectors()
                )
            )
        ).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @SuppressWarnings("unchecked")
    private Stream<Class<?>> classesFromClassSelectors() {
        return (Stream<Class<?>>) (Object) request.getSelectorsByType(ClassSelector.class).stream()
            .map(ClassSelector::getJavaClass)
            .filter(classFilter);
    }

    private Stream<Class<?>> classesFromPackageSelectors() {
        return request.getSelectorsByType(PackageSelector.class).stream()
            .map(s -> findAllClassesInPackage(s.getPackageName(), this.classFilter))
            .flatMap(Collection::stream);
    }

    private Stream<Class<?>> classesFromModuleSelectors() {
        return request.getSelectorsByType(ModuleSelector.class).stream()
            .map(s -> findAllClassesInModule(s.getModuleName(), this.classFilter))
            .flatMap(Collection::stream);
    }

    private Stream<Class<?>> classesFromClasspathRootSelectors() {
        return request.getSelectorsByType(ClasspathRootSelector.class).stream()
            .map(s -> findAllClassesInClasspathRoot(s.getClasspathRoot(), this.classFilter))
            .flatMap(Collection::stream);
    }
}
