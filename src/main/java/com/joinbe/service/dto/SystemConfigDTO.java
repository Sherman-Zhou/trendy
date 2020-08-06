package com.joinbe.service.dto;

import com.joinbe.domain.SystemConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link SystemConfig} entity.
 */
@Data
public class SystemConfigDTO implements Serializable {

    @ApiModelProperty(value = "轨迹保存时间")
    private long trajectoryReserveDays;

    @ApiModelProperty(value = "上次备份时间", hidden = true)
    private String lastBackupTime;

    @ApiModelProperty("里程数计算倍数")
    private BigDecimal mileageMultiple;

}
