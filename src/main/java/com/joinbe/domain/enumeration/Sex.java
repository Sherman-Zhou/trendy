package com.joinbe.domain.enumeration;

/**
 * The Sex enumeration.
 */
public enum Sex {

    MALE("M"), FEMALE("F"), UNKNOWN("U");

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
