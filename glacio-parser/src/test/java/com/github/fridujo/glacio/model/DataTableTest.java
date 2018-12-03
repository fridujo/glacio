package com.github.fridujo.glacio.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class DataTableTest {

    @Test
    void non_square_datatable() {
        DataTable dataTable = new DataTable(Arrays.asList(
            new DataTable.Row(Arrays.asList("test")),
            new DataTable.Row(Arrays.asList("test1", "test2"))
        ));

        assertThat(dataTable.isSquare()).isFalse();
        assertThat(dataTable.hasWidth(1)).isTrue();
        assertThat(dataTable.hasWidth(2)).isFalse();
    }

    @Test
    void square_datatable() {
        DataTable dataTable = new DataTable(Arrays.asList(
            new DataTable.Row(Arrays.asList("test1", "test2")),
            new DataTable.Row(Arrays.asList("test3", "test4"))
        ));

        assertThat(dataTable.isSquare()).isTrue();
        assertThat(dataTable.hasWidth(2)).isTrue();
        assertThat(dataTable.hasWidth(3)).isFalse();
    }
}
