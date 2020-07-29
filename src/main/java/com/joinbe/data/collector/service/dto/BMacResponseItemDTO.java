package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

public class BMacResponseItemDTO {

    private String data;

    @ApiModelProperty(value = "MAC")
    private String mac;

    @ApiModelProperty(value = "设备的imei")
    private String imei;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
