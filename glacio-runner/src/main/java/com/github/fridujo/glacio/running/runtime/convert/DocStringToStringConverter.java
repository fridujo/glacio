package com.github.fridujo.glacio.running.runtime.convert;

import com.github.fridujo.glacio.model.DocString;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.Value;

public class DocStringToStringConverter extends AbstractPositionedParameterConverter {
    @Override
    protected Value convert(Value rawValue, ParameterDescriptor parameterDescriptor) {
        return rawValue
            .filterType(DocString.class)
            .filter(String.class == parameterDescriptor.type)
            .map(DocString::getContent);
    }
}
