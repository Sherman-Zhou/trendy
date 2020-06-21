package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleStateDTO {

    @ApiModelProperty(value = "设备信息")
    private EquipmentDTO equipment;

    @ApiModelProperty(value = "最新定位信息")
    private VehicleTrajectoryDetailsDTO trajectoryDetails;

    @ApiModelProperty(value = "车辆总里程")
    private BigDecimal totalMileage;

    @ApiModelProperty(value = "剩余油量")
    private BigDecimal remainingFuel;
}
