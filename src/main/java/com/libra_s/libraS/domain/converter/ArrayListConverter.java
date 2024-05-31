package com.libra_s.libraS.domain.converter;

import jakarta.persistence.AttributeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayListConverter implements AttributeConverter<List<String>, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) {
            return null;
        }
        return String.join(SPLIT_CHAR, attribute);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .collect(Collectors.toList());
    }
}
