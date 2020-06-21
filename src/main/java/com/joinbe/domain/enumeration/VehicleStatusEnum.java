package com.joinbe.domain.enumeration;

public enum VehicleStatusEnum {

    RUNNING("R"), STOPPED("S"), UNKNOWN("U");

    private String code;

    VehicleStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static VehicleStatusEnum getByCode(String code) {
        for (VehicleStatusEnum vehicleStatus : VehicleStatusEnum.values()) {
            if (vehicleStatus.getCode().equals(code)) {
                return vehicleStatus;
            }
        }
        return null;
    }
}
