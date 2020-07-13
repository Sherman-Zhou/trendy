package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class BleVehicleReq {

    @NotEmpty(message = "plate number can't not be empty")
    @Length(min = 1, max = 10, message = "plate number's length should between 1~10")
    @ApiModelProperty(value = "车牌号", required = true)
    private String plateNumber;

    @NotEmpty(message = "Bluetooth name cant not be empty")
    @ApiModelProperty(value = "蓝牙的名称",required = true)
    private String bleName;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }
}
