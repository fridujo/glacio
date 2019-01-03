package com.github.fridujo.glacio.running.runtime.convert;

import static com.github.fridujo.glacio.running.runtime.convert.ParameterDescriptors.descriptorForMethodArgument;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.entry;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.github.fridujo.glacio.model.DataTable;
import com.github.fridujo.glacio.running.api.convert.Converter;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

class DataTableToMapConverterTest extends AbstractConverterTest {

    DataTableToMapConverterTest() {
        super(
            new DataTableToMapConverter(),
            new DataTable(Collections.emptyList()), Map.class,
            "test", String.class
        );
    }

    @Test
    void unsquare_datable_is_not_converted() {
        SourceSet sourceSet = SourceSet.fromRaw(
            table(
                row("test"),
                row("test1", "test2")
            )
        );
        Value converted = converterUnderTest.convert(sourceSet, validDescriptor());

        assertThat(converted.present).isFalse();
    }

    @Test
    void single_column_datable_is_not_converted() {
        SourceSet sourceSet = SourceSet.fromRaw(
            table(
                row("test"),
                row("test1")
            )
        );
        Value converted = converterUnderTest.convert(sourceSet, validDescriptor());

        assertThat(converted.present).isFalse();
    }

    @Test
    @SuppressWarnings("unchecked")
    void nominal_conversion_without_parameter_conversion() {
        SourceSet sourceSet = SourceSet.fromRaw(
            table(
                row("test", "test"),
                row("test1", "test2")
            )
        );
        Value converted = converterUnderTest.convert(sourceSet, validDescriptor());

        assertThat(converted.present).isTrue();
        assertThat(converted.value).isInstanceOf(Map.class);
        assertThat((Map<String, String>) converted.value)
            .containsExactly(
                entry("test", "test"),
                entry("test1", "test2")
            );
    }

    @Test
    @SuppressWarnings("unchecked")
    void nominal_conversion_with_parameter_conversion() {
        SourceSet sourceSet = SourceSet.fromRaw(
            table(
                row("3", "test1"),
                row("30_000", "test2")
            )
        );
        ParameterDescriptor parameterDescriptor = descriptorForMethodArgument(HaveOneMethod.class, "handleMap");

        Value converted = converterUnderTest.convert(sourceSet, parameterDescriptor);

        assertThat(converted.present).isTrue();
        assertThat(converted.value).isInstanceOf(Map.class);
        assertThat((Map<Integer, String>) converted.value)
            .containsExactly(
                entry(3, "test1"),
                entry(30_000, "test2")
            );
    }


    @Test
    void conversion_with_parameters_cannot_be_done_without_initialization() {
        SourceSet sourceSet = SourceSet.fromRaw(
            table(
                row("3", "test1"),
                row("30_000", "test2")
            )
        );
        ParameterDescriptor parameterDescriptor = descriptorForMethodArgument(HaveOneMethod.class, "handleMap");
        Converter converterUnderTest = new DataTableToMapConverter();

        assertThatExceptionOfType(IllegalStateException.class)
            .isThrownBy(() -> converterUnderTest.convert(sourceSet, parameterDescriptor))
            .withMessage("ParameterConverter must be set to perform parameter conversion");
    }

    private DataTable table(DataTable.Row... rows) {
        return new DataTable(Arrays.asList(rows));
    }

    private DataTable.Row row(String... cells) {
        return new DataTable.Row(Arrays.asList(cells));
    }

    interface HaveOneMethod {
        void handleMap(Map<Integer, String> map);
    }
}
