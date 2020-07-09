package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class EquipmentOpRecordVM implements Serializable {

//    @ApiModelProperty(value = "设备ID")
//    private String equipmentId;
//
//    @ApiModelProperty(value = "车牌")
//    private String licensePlateNumber;


    @ApiModelProperty(value = "用戶登陆名，设备ID或者车牌")
    private String userId;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "开始时间: yyyy-MM-dd")
    private String startDate;

    @ApiModelProperty(value = "结束时间: yyyy-MM-dd")
    private String endDate;
}
