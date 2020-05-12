package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link com.joinbe.domain.EquipmentFault} entity.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EquipmentFaultDTO implements Serializable {

    private Long id;

    /**
     * 报警类型： - 枚举类型: Event
     *                     Error
     */
    @Size(max = 50)
    @ApiModelProperty(value = "报警类型：", notes = "枚举类型: Event, Error")
    private String alertType;

    /**
     * 报警内容：\nMain power low event\nDevice Lost
     */
    @Size(max = 200)
    @ApiModelProperty(value = "报警内容")
    private String alertContent;

    /**
     * 是否已读
     */
    @ApiModelProperty(value = "是否已读")
    private Boolean isRead;

    @ApiModelProperty(value = "创建者")
    private String createdBy;

    @ApiModelProperty(value = "创建时间")
    private Instant createdDate;

    @ApiModelProperty(value = "最后更新者")
    private String lastModifiedBy;

    @ApiModelProperty(value = "最后更新时间")
    private Instant lastModifiedDate;

    @ApiModelProperty(value = "汽车主键")
    private Long vehicleId;

    @ApiModelProperty(value = "设备主键")
    private Long equipmentId;

}
