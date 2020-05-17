package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VehicleBindingVM extends VehicleVM {

    @ApiModelProperty(value = "设备ID")
    private String identifyNumber;
}
