package com.github.fridujo.glacio.running.runtime.convert;

import static com.github.fridujo.glacio.running.runtime.convert.ParameterDescriptors.descriptorForMethodArgument;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.running.api.convert.ConvertWith;
import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

class AnnotationBasedConverterTest {

    private final Converter converterUnderTest = new AnnotationBasedConverter();

    @Test
    void parameter_without_annotation_is_not_converted() {
        ParameterDescriptor descriptor = descriptorForMethodArgument(MethodSet.class, "withoutAnnotation");
        Value converted = converterUnderTest.convert(SourceSet.empty(), descriptor);

        assertThat(converted.present).isFalse();
    }

    @Test
    void parameter_with_direct_annotation_is_converted() {
        ParameterDescriptor descriptor = descriptorForMethodArgument(MethodSet.class, "withDirectAnnotation");
        Value converted = converterUnderTest.convert(SourceSet.empty(), descriptor);

        assertThat(converted.present).isTrue();
        assertThat(converted.value).isEqualTo("test");
    }

    @Test
    void parameter_with_indirect_annotation_is_converted() {
        ParameterDescriptor descriptor = descriptorForMethodArgument(MethodSet.class, "withIndirectAnnotation");
        Value converted = converterUnderTest.convert(SourceSet.empty(), descriptor);

        assertThat(converted.present).isTrue();
        assertThat(converted.value).isEqualTo("test");
    }

    @Test
    void invalid_converter_will_throw_during_convertion() {
        ParameterDescriptor descriptor = descriptorForMethodArgument(MethodSet.class, "withInvalidConverter");

        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> converterUnderTest.convert(SourceSet.empty(), descriptor))
            .withMessage("Specified Converter[" + InvalidConverter.class.getName() + "] must have a public no-args constructor");
    }

    interface MethodSet {
        void withoutAnnotation(String par);

        void withDirectAnnotation(@ConvertWith(TestConverter.class) String par);

        void withIndirectAnnotation(@ConvertForTest String par);

        void withInvalidConverter(@ConvertWith(InvalidConverter.class) String par);
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ConvertWith(TestConverter.class)
    @interface ConvertForTest {
    }

    public static final class TestConverter implements Converter {

        @Override
        public Value convert(SourceSet sourceSet, ParameterDescriptor parameterDescriptor) {
            return Value.present("test");
        }
    }

    private static final class InvalidConverter implements Converter {

        @Override
        public Value convert(SourceSet sourceSet, ParameterDescriptor parameterDescriptor) {
            // will no be used
            return null;
        }
    }
}
