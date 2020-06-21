package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class TrajectoryVM {

    @ApiModelProperty(value = "车辆主键")
    @NotBlank
    private Long vehicleId;

    @ApiModelProperty(value = "轨迹id")
    private String trajectoryId;

    @ApiModelProperty(value = "轨迹id列表")
    private List<String> trajectoryIds;

    @ApiModelProperty(value = "开始时间: yyyy-MM-dd")
    private String startDate;

    @ApiModelProperty(value = "结束时间: yyyy-MM-dd")
    private String endDate;
}
