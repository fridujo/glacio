package com.github.fridujo.glacio.running.runtime.convert;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.fridujo.glacio.model.DataTable;
import com.github.fridujo.glacio.running.api.convert.ParameterConverterAware;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.Value;

public class DataTableToMapConverter extends AbstractPositionedParameterConverter implements ParameterConverterAware {

    @Override
    protected Value convert(Value rawValue, ParameterDescriptor parameterDescriptor) {
        return rawValue
            .filterType(DataTable.class)
            .filterValue(DataTable::isSquare)
            .<DataTable>filterValue(d -> d.hasWidth(2))
            .filter(Map.class == parameterDescriptor.type || LinkedHashMap.class == parameterDescriptor.type)
            .<DataTable>mapPresent(dataTable -> transform(
                dataTable,
                parameterDescriptor.getTypeArgument(0),
                parameterDescriptor.getTypeArgument(1)
            ));
    }

    private <K, V> Map<K, V> transform(DataTable dataTable, Class<K> keyType, Class<V> valueType) {
        Map<K, V> map = new LinkedHashMap<>();
        dataTable
            .getRows()
            .forEach(row -> map.put(
                convertTo(row.getCells().get(0), keyType),
                convertTo(row.getCells().get(1), valueType)
            ));
        return map;
    }
}
