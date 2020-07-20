package com.joinbe.domain.enumeration;

public enum VehicleFireStatusEnum {

    OPEN_FIRE("OPEN_FIRE"), CLOSE_FIRE("CLOSE_FIRE"), UNKNOWN("UNKNOWN");

    private String code;

    VehicleFireStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static VehicleFireStatusEnum getByCode(String code) {
        for (VehicleFireStatusEnum vehicleStatus : VehicleFireStatusEnum.values()) {
            if (vehicleStatus.getCode().equals(code)) {
                return vehicleStatus;
            }
        }
        return null;
    }
}
