package com.joinbe.domain.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.HashMap;
import java.util.Map;

public enum RecordStatus {

    ACTIVE("A"), DELETED("D"), INACTIVE("I");

    private static final Map<String, RecordStatus> mapping= new HashMap<>(5);

    static {
        RecordStatus [] statuses = values();
        for(RecordStatus status : statuses) {
            mapping.put(status.getCode(), status);
        }
    }

    @EnumValue
    private String code;

    RecordStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static RecordStatus resolve(String code) {
        return code != null ? mapping.get(code): null;
    }
}
