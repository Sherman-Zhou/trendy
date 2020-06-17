package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;
import java.io.Serializable;

public class DeviceResponseItem implements Serializable {

    /**
     * 设备ID
     */
    @ApiModelProperty(value = "设备ID")
    private String deviceId;

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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
