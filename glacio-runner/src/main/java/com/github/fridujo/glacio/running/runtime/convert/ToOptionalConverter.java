package com.github.fridujo.glacio.running.runtime.convert;

import java.util.Optional;

import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.Value;

public class ToOptionalConverter extends AbstractPositionedParameterConverter {
    @Override
    protected Value convert(Value rawValue, ParameterDescriptor parameterDescriptor) {
        final Value converted;
        if (Optional.class != parameterDescriptor.type) {
            converted = Value.absent();
        } else {
            converted = rawValue
                .mapPresent(o -> this.convertTo(o, parameterDescriptor.getTypeArgument(0)))
                .mapPresent(Optional::ofNullable, Value.present(Optional.empty()));
        }
        return converted;
    }
}
