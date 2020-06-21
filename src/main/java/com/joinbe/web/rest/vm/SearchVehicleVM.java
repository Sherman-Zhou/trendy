package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchVehicleVM {

    @ApiModelProperty("车牌号或者设备id")
    private String licensePlateNumberOrDeviceId;

    @ApiModelProperty("是否只看在线车辆")
    private Boolean onlineOnly;

    @ApiModelProperty(value = "部门主键")
    private Long divisionId;
}
