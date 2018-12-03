package com.github.fridujo.glacio.running.api.convert;

public interface ParameterConverter {

    Object convert(SourceSet sourceSet, ParameterDescriptor parameterDescriptor);
}
