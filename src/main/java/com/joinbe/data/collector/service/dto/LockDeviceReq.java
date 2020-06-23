package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LockDeviceReq {

    @NotNull(message = "vehicle id cant not be empty")
    @ApiModelProperty(value = "车辆的ID",required = true)
    private Long vehicleId;

    @NotEmpty(message = "mode cant not be empty")
    @ApiModelProperty(value = "锁的模式，open - 打开， close -关闭",required = true)
    private String mode;

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
