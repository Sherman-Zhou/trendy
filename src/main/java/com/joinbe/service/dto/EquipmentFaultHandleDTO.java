package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link com.joinbe.domain.EquipmentFault} entity.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EquipmentFaultHandleDTO implements Serializable {

    @ApiModelProperty(value = "设备异常主键")
    @NotNull
    @Positive
    private Long id;

    /**
     * 处理意见
     */
    @ApiModelProperty(value = "处理意见")
    @NotBlank
    private String remark;

//    /**
//     * 处理时间
//     */
//    @ApiModelProperty(value = "处理时间")
//    private Instant handledOn;

}
