package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EquipmentVM implements Serializable {

    @ApiModelProperty(value = "设备ID")
    private String identifyNumber;

    @ApiModelProperty(value = "设备IMEI")
    private String imei;

    @ApiModelProperty(value = "SIM卡号")
    private String simCardNum;

    @ApiModelProperty(value = "是否在线")
    private Boolean isOnline;

    @ApiModelProperty(value = "是否已绑定")
    private Boolean isBounded;
}
