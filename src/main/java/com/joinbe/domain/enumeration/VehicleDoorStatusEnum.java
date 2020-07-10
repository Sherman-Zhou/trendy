package com.joinbe.domain.enumeration;

public enum VehicleDoorStatusEnum {

    OPEN("OPEN"), CLOSE("CLOSE"), UNKNOWN("UNKNOWN");

    private String code;

    VehicleDoorStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static VehicleDoorStatusEnum getByCode(String code) {
        for (VehicleDoorStatusEnum vehicleStatus : VehicleDoorStatusEnum.values()) {
            if (vehicleStatus.getCode().equals(code)) {
                return vehicleStatus;
            }
        }
        return null;
    }
}
