package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

public class BleResponseItemDTO {

    private String data;

    private String ok;

    private String type;

    @ApiModelProperty(value = "蓝牙名称")
    private String bleName;

    @ApiModelProperty(value = "设备的imei")
    private String imei;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
