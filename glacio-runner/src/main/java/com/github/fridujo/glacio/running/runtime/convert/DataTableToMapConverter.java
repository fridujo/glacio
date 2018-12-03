package com.github.fridujo.glacio.running.runtime.convert;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.fridujo.glacio.model.DataTable;
import com.github.fridujo.glacio.running.api.convert.ParameterConverter;
import com.github.fridujo.glacio.running.api.convert.ParameterConverterAware;
import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.SourceSet;
import com.github.fridujo.glacio.running.api.convert.Value;

public class DataTableToMapConverter extends AbstractPositionedParameterConverter implements ParameterConverterAware {

    private ParameterConverter parameterConverter = null;

    @Override
    protected Value convert(Value rawValue, ParameterDescriptor parameterDescriptor) {
        return rawValue
            .filterType(DataTable.class)
            .filterValue(DataTable::isSquare)
            .<DataTable>filterValue(d -> d.hasWidth(2))
            .filter(Map.class == parameterDescriptor.type || LinkedHashMap.class == parameterDescriptor.type)
            .<DataTable>map(dataTable -> transform(
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

    @SuppressWarnings("unchecked")
    private <T> T convertTo(String stringValue, Class<T> keyType) {
        if (keyType == null || String.class == keyType) {
            return (T) stringValue;
        }
        if (parameterConverter == null) {
            throw new IllegalStateException("ParameterConverter must be set to perform parameter conversion");
        }
        return (T) parameterConverter.convert(SourceSet.fromRaw(stringValue), new ParameterDescriptor(0, keyType, keyType, null));
    }

    @Override
    public void setConverter(ParameterConverter parameterConverter) {
        this.parameterConverter = parameterConverter;
    }
}
