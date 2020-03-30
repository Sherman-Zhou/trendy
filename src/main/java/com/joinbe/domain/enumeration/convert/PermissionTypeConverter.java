package com.joinbe.domain.enumeration.convert;

import com.joinbe.domain.enumeration.PermissionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class PermissionTypeConverter implements AttributeConverter<PermissionType, String> {

    @Override
    public String convertToDatabaseColumn(PermissionType permissionType) {
        if (permissionType == null) {
            return null;
        }
        return permissionType.getCode();
    }

    @Override
    public PermissionType convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(PermissionType.values())
            .filter(c -> c.getCode().equals(code))
            .findFirst()
            .orElse(PermissionType.FOLDER);
    }
}
