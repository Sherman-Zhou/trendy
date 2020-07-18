package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class BleDeviceReq {

    @NotEmpty(message = "device IMEI number cant not be empty")
    @Length(min = 1, max = 100, message = "device IMEI number length should between 1~100")
    @ApiModelProperty(value = "设备IMEI", required=true)
    private String imei;

    @NotEmpty(message = "Bluetooth name cant not be empty")
    @ApiModelProperty(value = "蓝牙的名称",required = true)
    private String bleName;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }
}
