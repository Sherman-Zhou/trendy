package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VehicleSummaryDTO {

    @ApiModelProperty(value = "车辆主键")
    private Long id;

    @ApiModelProperty(value = "车牌")
    private String licensePlateNumber;

    @ApiModelProperty(value = "是否行驶中")
    private Boolean isMoving;

    @ApiModelProperty(value = "部门主键")
    private Long divisionId;

}
