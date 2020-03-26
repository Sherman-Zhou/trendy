package com.joinbe.common.util;

import org.springframework.beans.BeanUtils;

public class BeanConverter {

    private static final String[] DEFAULT_IGNORE_PROPERTIES = new String[]{"createdBy", "createdDate", "lastModifiedBy", "lastModifiedDate"};

    public static <T, U> void copyProperties(T t, U u) {
        copyProperties(t, u, false);
    }

    public static <T, U> void copyProperties(T t, U u, boolean ignoreAuditingFields) {
        if (ignoreAuditingFields) {
            BeanUtils.copyProperties(t, u, DEFAULT_IGNORE_PROPERTIES);
        } else {
            BeanUtils.copyProperties(t, u);
        }
    }

    public static <T, U> U toDto(T t, Class<U> clazz) {
        U u = null;
        try {
            u = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        copyProperties(t, u);
        return u;
    }


    public static <T, U> U toEntity(T t, Class<U> clazz) {
        U u = null;
        try {
            u = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        copyProperties(t, u, true);
        return u;
    }
}
