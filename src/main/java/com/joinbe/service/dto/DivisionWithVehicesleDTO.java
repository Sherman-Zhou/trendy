package com.joinbe.service.dto;

import com.joinbe.domain.Division;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DivisionWithVehicesleDTO extends DivisionDTO {

    @ApiModelProperty(value = "车辆")
    private List<VehicleSummaryDTO> vehicles;

    public DivisionWithVehicesleDTO(Division division) {
        this.setId(division.getId());
        this.setName(division.getName());
        this.setDescription(division.getDescription());
        this.setParentId(division.getParentId());

        this.setCode(division.getCode());
        this.setStatus(division.getStatus());
    }
}
