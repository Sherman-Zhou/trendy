package com.joinbe.domain.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum PermissionType {
    FOLDER("F"),
    MENU("M"),
    OPERATION("O");

    @EnumValue
    private String code;

    PermissionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
