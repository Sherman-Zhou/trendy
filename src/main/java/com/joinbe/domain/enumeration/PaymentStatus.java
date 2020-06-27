package com.joinbe.domain.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.HashMap;
import java.util.Map;

public enum PaymentStatus {

    // Settled
    SETTLED("S"), UNSETTLED("U");

    private static final Map<String, PaymentStatus> mapping = new HashMap<>(3);

    static {
        PaymentStatus[] values = values();
        for (PaymentStatus paymentStatus : values) {
            mapping.put(paymentStatus.getCode(), paymentStatus);
        }
    }

    @EnumValue
    private String code;

    PaymentStatus(String code) {
        this.code = code;
    }

    public static PaymentStatus resolve(String code) {
        return code != null ? mapping.get(code) : null;
    }

    public static String getCode(PaymentStatus paymentStatus) {
        return paymentStatus != null ? paymentStatus.getCode() : null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
