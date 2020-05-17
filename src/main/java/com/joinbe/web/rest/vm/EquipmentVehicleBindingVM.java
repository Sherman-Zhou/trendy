package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EquipmentVehicleBindingVM implements Serializable {

    @ApiModelProperty(value = "设备主键")
    private Long equipmentId;

    @ApiModelProperty(value = "车辆主键")
    private Long vehicleId;
}
