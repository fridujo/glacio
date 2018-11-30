package com.github.fridujo.glacio.running.api.convert;

public interface Converter {

    Value convert(SourceSet sourceSet, ParameterDescriptor parameterDescriptor);
}
