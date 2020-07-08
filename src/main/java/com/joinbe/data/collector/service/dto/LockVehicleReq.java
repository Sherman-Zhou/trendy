package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class LockVehicleReq {

    @NotEmpty(message = "plate number can't not be empty")
    @Length(min = 1, max = 10, message = "plate number's length should between 1~10")
    @ApiModelProperty(value = "车牌号", required = true)
    private String plateNumber;

    @NotEmpty(message = "mode cant not be empty")
    @ApiModelProperty(value = "锁的模式，open - 打开， close -关闭",required = true)
    private String mode;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
