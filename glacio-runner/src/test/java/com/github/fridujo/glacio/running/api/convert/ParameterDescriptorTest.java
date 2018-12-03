package com.github.fridujo.glacio.running.api.convert;

import static com.github.fridujo.glacio.running.runtime.convert.ParameterDescriptors.TypeReference;
import static com.github.fridujo.glacio.running.runtime.convert.ParameterDescriptors.descriptor;
import static com.github.fridujo.glacio.running.runtime.convert.ParameterDescriptors.descriptorForMethodArgument;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

class ParameterDescriptorTest {

    @Test
    void type_argument_returns_matching_or_null() {
        ParameterDescriptor descriptor = descriptor(new TypeReference<Optional<Integer>>() {
        });

        assertThat(descriptor.getTypeArgument(0)).isEqualTo(Integer.class);
        assertThat(descriptor.getTypeArgument(1)).isNull();
    }

    @Test
    void nested_type_argument_is_converted_to_class() {
        ParameterDescriptor descriptor = descriptor(new TypeReference<Optional<Supplier<Integer>>>() {
        });

        assertThat(descriptor.getTypeArgument(0)).isEqualTo(Supplier.class);
    }

    @Test
    void toString_displays_debug_information() {
        ParameterDescriptor descriptor = descriptorForMethodArgument(Consumer.class, "accept", 0);

        assertThat(descriptor).hasToString("Parameter(position=0, type=class java.lang.Object) "
            + "of public abstract void java.util.function.Consumer.accept(java.lang.Object)");
    }
}
