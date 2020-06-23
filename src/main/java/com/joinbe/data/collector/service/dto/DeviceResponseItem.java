package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class DeviceResponseItem implements Serializable {

    /**
     * 设备型号
     */
    @ApiModelProperty(value = "设备型号")
    private String identifyNumber;

    /**
     * 设备IMEI
     */
    @ApiModelProperty(value = "设备IMEI")
    private String imei;

    /**
     * 固件版本
     */
    @ApiModelProperty(value = "固件版本")
    private String version;

    public String getIdentifyNumber() {
        return identifyNumber;
    }

    public void setIdentifyNumber(String identifyNumber) {
        this.identifyNumber = identifyNumber;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
