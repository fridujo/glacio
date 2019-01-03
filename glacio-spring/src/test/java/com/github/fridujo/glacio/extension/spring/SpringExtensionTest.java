package com.github.fridujo.glacio.extension.spring;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.github.fridujo.glacio.running.api.configuration.GlacioConfiguration;
import com.github.fridujo.glacio.running.api.extension.ExtendGlacioWith;

@GlacioConfiguration(
    featurePaths = "classpath:",
    gluePaths = "com.github.fridujo.glacio.extension.spring"
)
@ExtendGlacioWith(SpringExtension.class)
@ContextConfiguration(classes = TestApp.class)
@TestPropertySource(properties = "my.prop=45.3D")
class SpringExtensionTest {
}
