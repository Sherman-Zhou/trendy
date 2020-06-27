package com.joinbe.domain.enumeration.convert;

import com.joinbe.domain.enumeration.PaymentStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, String> {

    @Override
    public String convertToDatabaseColumn(PaymentStatus sex) {
        if (sex == null) {
            return null;
        }
        return sex.getCode();
    }

    @Override
    public PaymentStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(PaymentStatus.values())
            .filter(c -> c.getCode().equals(code))
            .findFirst()
            .orElse(PaymentStatus.UNSETTLED);
    }
}
