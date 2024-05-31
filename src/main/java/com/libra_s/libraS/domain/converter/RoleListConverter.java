package com.libra_s.libraS.domain.converter;

import com.libra_s.libraS.domain.enums.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class RoleListConverter implements AttributeConverter<List<Role>, String> {

    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<Role> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream().map(Enum::name).collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<Role> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return Arrays.stream(dbData.split(SPLIT_CHAR)).map(Role::valueOf).collect(Collectors.toList());
    }
}
