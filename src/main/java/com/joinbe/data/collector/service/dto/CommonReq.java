package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class CommonReq {

    @NotEmpty(message = "device IMEI number cant not be empty")
    @Length(min = 1, max = 100, message = "device IMEI number length should between 1~100")
    @ApiModelProperty(value = "设备IMEI", required=true)
    private String imei;

    @ApiModelProperty(value = "命令, 命令末尾不带回车换行符",required = false)
    private String cmd;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "CommonReq{" +
            "imei='" + imei + '\'' +
            ", cmd='" + cmd + '\'' +
            '}';
    }
}
