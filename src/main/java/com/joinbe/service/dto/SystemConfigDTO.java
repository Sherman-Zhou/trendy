package com.joinbe.service.dto;

import com.joinbe.domain.SystemConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link SystemConfig} entity.
 */
@Data
public class SystemConfigDTO implements Serializable {

    @ApiModelProperty(value = "轨迹保存时间")
    private long trajectoryReserveDays;

    @ApiModelProperty(value = "上次备份时间", hidden = true)
    private String lastBackupTime;

}
