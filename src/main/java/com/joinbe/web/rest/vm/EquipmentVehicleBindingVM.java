package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
public class EquipmentVehicleBindingVM implements Serializable {

    @ApiModelProperty(value = "设备主键")
    private Long equipmentId;

    @ApiModelProperty(value = "车辆主键")
    private Long vehicleId;

    @ApiModelProperty(value = "讯号方向")
    @Min(0)
    @Max(1)
    private Long signalInd;


}
