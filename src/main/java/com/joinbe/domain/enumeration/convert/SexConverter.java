package com.joinbe.domain.enumeration.convert;

import com.joinbe.domain.enumeration.Sex;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class SexConverter implements AttributeConverter<Sex, String> {

    @Override
    public String convertToDatabaseColumn(Sex sex) {
        if (sex == null) {
            return null;
        }
        return sex.getCode();
    }

    @Override
    public Sex convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(Sex.values())
            .filter(c -> c.getCode().equals(code))
            .findFirst()
            .orElse(Sex.UNKNOWN);
    }
}
