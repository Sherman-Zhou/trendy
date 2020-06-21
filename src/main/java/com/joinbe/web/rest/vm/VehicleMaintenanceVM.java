package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VehicleMaintenanceVM {

    @ApiModelProperty(value = "车辆主键")
    @NotBlank
    private String vehicleId;
}
