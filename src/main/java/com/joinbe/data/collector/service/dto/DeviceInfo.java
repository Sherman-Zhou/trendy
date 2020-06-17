package com.joinbe.data.collector.service.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class DeviceInfo {

    @NotEmpty(message = "plate number cant not be empty")
    @Length(min = 1, max = 10, message = "plate number's length should between 1~10")
    private String plateNumber;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
}