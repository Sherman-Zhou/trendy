package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class LockDeviceReq {

    @NotEmpty(message = "device id cant not be empty")
    @Length(min = 1, max = 100, message = "device id's length should between 1~100")
    @ApiModelProperty(value = "设备Id")
    private String deviceId;

    @NotEmpty(message = "mode cant not be empty")
    @ApiModelProperty(value = "锁的模式，open - 打开， close -关闭")
    private String mode;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
