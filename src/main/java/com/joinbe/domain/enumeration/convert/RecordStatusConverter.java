package com.joinbe.domain.enumeration.convert;

import com.joinbe.domain.enumeration.RecordStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RecordStatusConverter implements AttributeConverter<RecordStatus, String> {

    @Override
    public String convertToDatabaseColumn(RecordStatus recordStatus) {
        if (recordStatus == null) {
            return null;
        }
        return recordStatus.getCode();
    }

    @Override
    public RecordStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(RecordStatus.values())
            .filter(c -> c.getCode().equals(code))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }
}
