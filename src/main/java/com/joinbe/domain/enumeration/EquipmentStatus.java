package com.joinbe.domain.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.HashMap;
import java.util.Map;

public enum EquipmentStatus {


    BOUND("B"), UNBOUND("U"), DELETED("D");

    private static final Map<String, EquipmentStatus> mapping = new HashMap<>(3);

    static {
        EquipmentStatus[] statuses = values();
        for (EquipmentStatus status : statuses) {
            mapping.put(status.getCode(), status);
        }
    }

    @EnumValue
    private String code;

    EquipmentStatus(String code) {
        this.code = code;
    }

    public static EquipmentStatus resolve(String code) {
        return code != null ? mapping.get(code) : UNBOUND;
    }

    public static String getCode(EquipmentStatus status) {
        return status != null ? status.getCode() : null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
