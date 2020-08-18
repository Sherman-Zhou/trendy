package com.joinbe.domain.enumeration;

public enum DoorSignalEnum {

    POSITIVE("POSITIVE"), NEGATIVE("NEGATIVE");

    private String code;

    DoorSignalEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static DoorSignalEnum getByCode(String code) {
        for (DoorSignalEnum doorSignal : DoorSignalEnum.values()) {
            if (doorSignal.getCode().equals(code)) {
                return doorSignal;
            }
        }
        return null;
    }
}
