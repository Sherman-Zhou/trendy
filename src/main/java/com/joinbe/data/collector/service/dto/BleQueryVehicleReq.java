package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class BleQueryVehicleReq {

    @NotEmpty(message = "plate number can't not be empty")
    @Length(min = 1, max = 10, message = "plate number's length should between 1~10")
    @ApiModelProperty(value = "车牌号", required = true)
    private String plateNumber;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}
