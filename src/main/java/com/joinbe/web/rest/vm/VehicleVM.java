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

    @ApiModelProperty(value = "门店主键")
    private String shopId;

//    @ApiModelProperty(value = "年份")
//    private String prodYear;

    @ApiModelProperty(value = "车牌")
    private String licensePlateNumber;

    @ApiModelProperty(value = "是否在线")
    private Boolean isOnline;

    @ApiModelProperty(value = "是否已绑定")
    private Boolean isBounded;


}
