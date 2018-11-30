package com.github.fridujo.glacio.running.runtime.convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.github.fridujo.glacio.running.api.convert.ParameterDescriptor;
import com.github.fridujo.glacio.running.api.convert.Value;

public class ToNumberConverter extends AbstractPositionedParameterConverter {

    @Override
    protected Value convert(Value rawValue, ParameterDescriptor parameterDescriptor) {
        final Value value;
        Class<?> parameterType = parameterDescriptor.type;
        boolean targetIsNumber = Number.class.isAssignableFrom(parameterType);
        if (targetIsNumber && rawValue.present) {
            value = parseNumber(String.valueOf(rawValue.value), (Class<? extends Number>) parameterType);
        } else {
            value = Value.absent();
        }
        return value;
    }

    private Value parseNumber(CharSequence cs, Class<? extends Number> parameterType) throws NumberFormatException {
        String numberAsString = removeWhitespacesAndUnderscores(cs.toString());
        final Value value;
        if (Byte.class == parameterType) {
            value = Value.present(Byte.decode(numberAsString));
        } else if (Short.class == parameterType) {
            value = Value.present(Short.decode(numberAsString));
        } else if (Integer.class == parameterType) {
            value = Value.present(Integer.decode(numberAsString));
        } else if (Long.class == parameterType) {
            value = Value.present(Long.decode(numberAsString));
        } else if (BigInteger.class.isAssignableFrom(parameterType)) {
            value = Value.present(decodeBigInteger(numberAsString));
        } else if (Float.class == parameterType) {
            value = Value.present(Float.valueOf(numberAsString));
        } else if (Double.class == parameterType) {
            value = Value.present(Double.valueOf(numberAsString));
        } else {
            value = Value.present(new BigDecimal(numberAsString));
        }
        return value;
    }

    private String removeWhitespacesAndUnderscores(String s) {
        int length = s.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c) && '_' != c) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        if (value.startsWith("-")) {
            negative = true;
            ++index;
        }

        if (!value.startsWith("0x", index) && !value.startsWith("0X", index)) {
            if (value.startsWith("#", index)) {
                ++index;
                radix = 16;
            } else if (value.startsWith("0", index) && value.length() > 1 + index) {
                ++index;
                radix = 8;
            }
        } else {
            index += 2;
            radix = 16;
        }

        BigInteger result = new BigInteger(value.substring(index), radix);
        return negative ? result.negate() : result;
    }
}
