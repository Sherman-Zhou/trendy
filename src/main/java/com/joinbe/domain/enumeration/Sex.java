package com.joinbe.domain.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.HashMap;
import java.util.Map;

/**
 * The Sex enumeration.
 */
public enum Sex {

    MALE("M"), FEMALE("F"), UNKNOWN("U");

    private static final Map<String, Sex> mapping = new HashMap<>(3);

    static {
        Sex[] sexes = values();
        for (Sex sex : sexes) {
            mapping.put(sex.getCode(), sex);
        }
    }

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

    public static Sex resolve(String code) {
        return code != null ? mapping.get(code) : null;
    }

    public static String getCode(Sex sex) {
        return sex != null ? sex.getCode() : null;
    }
}
