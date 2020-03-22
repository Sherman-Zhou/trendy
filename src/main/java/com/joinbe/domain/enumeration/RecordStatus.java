package com.joinbe.domain.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum RecordStatus {

    ACTIVE("A"), DELETED("D"), INACTIVE("I");

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
}
