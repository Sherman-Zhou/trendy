package com.joinbe.domain.enumeration;

public enum IbuttonStatusEnum {

    ATTACHED("A"), REMOVED("R"), UNKNOWN("U");

    private String code;

    IbuttonStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static IbuttonStatusEnum getByCode(String code) {
        for (IbuttonStatusEnum vehicleStatus : IbuttonStatusEnum.values()) {
            if (vehicleStatus.getCode().equals(code)) {
                return vehicleStatus;
            }
        }
        return null;
    }
}
