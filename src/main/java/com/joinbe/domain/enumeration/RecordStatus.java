package com.joinbe.domain.enumeration;

public enum RecordStatus {

    ACTIVE("A"), DELETED("D"), INACTIVE("I");

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
