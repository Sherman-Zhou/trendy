package com.joinbe.domain.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * The Sex enumeration.
 */
public enum Sex {

    MALE("M"), FEMALE("F"), UNKNOWN("U");

    @EnumValue
    private String code;

    Sex(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
