package com.github.fridujo.glacio.junit.engine;

public interface GlacioProperties {

    String ENGINE_ID = "glacio";
    String GROUP_ID = "com.github.fridujo";
    String ARTIFACT_ID = "glacio-junit-engine";

    String DISABLED_PROPERTY_NAME = ARTIFACT_ID + ".disabled";
    String GLUE_PATHS_PARAMETER = ARTIFACT_ID + ".gluePaths";
    String FEATURE_PATHS_PARAMETER = ARTIFACT_ID + ".featurePaths";
}
