package com.joinbe.domain.enumeration.convert;

import com.joinbe.domain.enumeration.EquipmentStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class EquipmentStatusConverter implements AttributeConverter<EquipmentStatus, String> {

    @Override
    public String convertToDatabaseColumn(EquipmentStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();
    }

    @Override
    public EquipmentStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(EquipmentStatus.values())
            .filter(c -> c.getCode().equals(code))
            .findFirst()
            .orElse(EquipmentStatus.UNBOUND);
    }
}
