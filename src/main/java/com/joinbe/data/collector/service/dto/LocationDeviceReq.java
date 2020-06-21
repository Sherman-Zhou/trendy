package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class LocationDeviceReq {

    @NotEmpty(message = "device id cant not be empty")
    @Length(min = 1, max = 100, message = "device id's length should between 1~100")
    @ApiModelProperty(value = "设备Id")
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
