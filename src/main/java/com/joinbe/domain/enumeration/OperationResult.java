package com.joinbe.domain.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.HashMap;
import java.util.Map;

public enum OperationResult {
    SUCCESS("success"),//成功
    FAILURE("failure");// 失败

    private static final Map<String, OperationResult> mapping = new HashMap<>(6);

    @EnumValue
    private String code;

    static {
        OperationResult[] operationResults = values();
        for (OperationResult operationResult : operationResults) {
            mapping.put(operationResult.getCode(), operationResult);
        }
    }

    OperationResult(String code) {
        this.code = code;
    }

    public static String getCode(OperationResult operationResult) {
        return operationResult != null ? operationResult.getCode() : null;
    }

    public static OperationResult resolve(String code) {
        return code != null ? mapping.get(code) : null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
