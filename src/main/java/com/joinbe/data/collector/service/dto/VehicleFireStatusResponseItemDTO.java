package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

public class VehicleFireStatusResponseItemDTO {

    @ApiModelProperty(value = "车辆点火状态, OPEN_FIRE:点火状态; CLOSE_FIRE: 熄火状态; UNKNOWN: 未知状态")
    private String fireStatus;

    @ApiModelProperty(value = "设备的imei")
    private String imei;

    public String getFireStatus() {
        return fireStatus;
    }

    public void setFireStatus(String fireStatus) {
        this.fireStatus = fireStatus;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
