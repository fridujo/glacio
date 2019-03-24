package com.github.fridujo.glacio.junit.engine.discovery;

import java.util.Optional;
import java.util.function.Predicate;

import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.UniqueIdSelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

import com.github.fridujo.glacio.junit.engine.descriptor.GlacioConfigurationDescriptor;
import com.github.fridujo.glacio.running.runtime.configuration.ConfigurationContextBuilder;

public class GlacioConfigurationClassSelectorResolver implements SelectorResolver {

    private final IsGlacioConfigurationClass isGlacioConfigurationClass = new IsGlacioConfigurationClass();
    private final ConfigurationContextBuilder configurationContextBuilder = new ConfigurationContextBuilder();
    private final Predicate<String> classNameFilter;

    public GlacioConfigurationClassSelectorResolver(Predicate<String> classNameFilter) {
        this.classNameFilter = classNameFilter;
    }

    @Override
    public Resolution resolve(ClassSelector selector, Context context) {
        Class<?> testClass = selector.getJavaClass();
        if (isGlacioConfigurationClass.test(testClass)) {
            if (classNameFilter.test(testClass.getName())) {
                Optional<GlacioConfigurationDescriptor> gcd = context.addToParent(parent -> Optional.of(newClassTestDescriptor(parent, testClass)));
                return gcd.map(g -> Resolution.match(Match.exact(g))).orElse(Resolution.unresolved());
            }
        }
        return Resolution.unresolved();
    }

    @Override
    public Resolution resolve(UniqueIdSelector selector, Context context) {
        UniqueId uniqueId = selector.getUniqueId();
        UniqueId.Segment lastSegment = uniqueId.getLastSegment();
        if (GlacioConfigurationDescriptor.SEGMENT_TYPE.equals(lastSegment.getType())) {
            String className = lastSegment.getValue();
            Optional<GlacioConfigurationDescriptor> gcd = ReflectionUtils.tryToLoadClass(className)
                .toOptional()
                .filter(isGlacioConfigurationClass)
                .flatMap(testClass -> context.addToParent(parent -> Optional.of(newClassTestDescriptor(parent, testClass))));
            return gcd.map(g -> Resolution.match(Match.exact(g))).orElse(Resolution.unresolved());
        }
        return Resolution.unresolved();
    }

    private GlacioConfigurationDescriptor newClassTestDescriptor(TestDescriptor parent, Class<?> testClass) {
        return new GlacioConfigurationDescriptor(parent.getUniqueId(), configurationContextBuilder.fromClass(testClass));
    }
}
