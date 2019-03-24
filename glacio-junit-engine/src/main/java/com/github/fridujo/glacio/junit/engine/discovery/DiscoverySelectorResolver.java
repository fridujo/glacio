package com.github.fridujo.glacio.junit.engine.discovery;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver;

import com.github.fridujo.glacio.junit.engine.GlacioProperties;
import com.github.fridujo.glacio.junit.engine.descriptor.GlacioEngineDescriptor;
import com.github.fridujo.glacio.running.logging.Logger;
import com.github.fridujo.glacio.running.logging.LoggerFactory;

public class DiscoverySelectorResolver {

    private static final Logger logger = LoggerFactory.getLogger(DiscoverySelectorResolver.class);

    private static final EngineDiscoveryRequestResolver<GlacioEngineDescriptor> resolver = EngineDiscoveryRequestResolver.<GlacioEngineDescriptor>builder()
        .addClassContainerSelectorResolver(new IsGlacioConfigurationClass())
        .addSelectorResolver(context -> new GlacioConfigurationClassSelectorResolver(context.getClassNameFilter()))
        .addTestDescriptorVisitor(context -> TestDescriptor::prune)
        .build();

    @SuppressWarnings("unchecked")
    public void resolve(EngineDiscoveryRequest request, GlacioEngineDescriptor engineDescriptor) {
        if (glacioDisabled(request)) {
            logger.info("Glacio disabled");
            return;
        }
        resolver.resolve(request, engineDescriptor);
    }

    private boolean glacioDisabled(EngineDiscoveryRequest request) {
        return request
            .getConfigurationParameters()
            .get(GlacioProperties.DISABLED_PROPERTY_NAME)
            .filter("true"::equals)
            .isPresent();
    }
}
