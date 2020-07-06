package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class UploadEventLogReq {

    @NotEmpty(message = "device IMEI number cant not be empty")
    @Length(min = 1, max = 100, message = "device IMEI number length should between 1~100")
    @ApiModelProperty(value = "设备IMEI", required=true)
    private String imei;

    @NotEmpty(message = "Lock mode cant not be empty")
    @ApiModelProperty(value = "蓝牙开锁的模式，lock - 关锁， unlock -开锁",required = true)
    private String mode;

    @NotEmpty(message = "Lock or unlock result cant not be empty")
    @ApiModelProperty(value = "蓝牙开锁模式的结果，success - 成功， failure -失败",required = true)
    private String eventResult;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getEventResult() {
        return eventResult;
    }

    public void setEventResult(String eventResult) {
        this.eventResult = eventResult;
    }
}
