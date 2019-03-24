package com.github.fridujo.glacio.junit.engine;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.testkit.engine.EngineTestKit;

class EngineTests {

    @Test
    void execution_feature_events() {
        EngineTestKit.engine("glacio")
            .selectors(DiscoverySelectors.selectPackage("com.github")) // selects DiscoveryTests$TestInitializer
            .execute()
            .all()
            .assertStatistics(stats -> stats.started(5).skipped(2).finished(5));
    }
}
