package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleVM implements Serializable {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "部门主键")
    private String divisionId;

//    @ApiModelProperty(value = "年份")
//    private String prodYear;

    @ApiModelProperty(value = "车牌")
    private String licensePlateNumber;

    @ApiModelProperty(value = "是否在线")
    private Boolean isOnline;

    @ApiModelProperty(value = "是否已绑定")
    private Boolean isBounded;

    @ApiModelProperty("车辆状态： A-正常，  D-已删除, I-已隐藏")
    private String status;


}
