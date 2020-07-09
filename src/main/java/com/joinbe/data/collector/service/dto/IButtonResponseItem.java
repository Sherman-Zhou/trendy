package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class IButtonResponseItem implements Serializable {

    /**
     * 设备iButton的状态
     */
    @ApiModelProperty(value = "设备iButton的状态, A:吸附; R:移除; U:未知")
    private String iButtonStatus;

    /**
     * 设备iButton的Id
     */
    @ApiModelProperty(value = "设备iButton的ID")
    private String iButtonId;

    /**
     * 设备IMEI
     */
    @ApiModelProperty(value = "设备IMEI")
    private String imei;

    public String getiButtonStatus() {
        return iButtonStatus;
    }

    public void setiButtonStatus(String iButtonStatus) {
        this.iButtonStatus = iButtonStatus;
    }

    public String getiButtonId() {
        return iButtonId;
    }

    public void setiButtonId(String iButtonId) {
        this.iButtonId = iButtonId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
