package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class VehicleSummaryDTO {

    @ApiModelProperty(value = "车辆主键")
    private Long id;

    @ApiModelProperty(value = "车牌")
    private String licensePlateNumber;

    @ApiModelProperty(value = "是否行驶中")
    private Boolean isMoving;

    @ApiModelProperty(value = "是否在线")
    private Boolean isOnline;

    @ApiModelProperty(value = "门店主键")
    private String divisionId;

    @Size(max = 50)
    @ApiModelProperty(value = "名称")
    private String name;

}
