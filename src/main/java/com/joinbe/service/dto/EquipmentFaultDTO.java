package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
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

    @ApiModelProperty(value = "设备ID")
    private String identifyNumber;

    @ApiModelProperty(value = "组织")
    private String orgName;

    @ApiModelProperty(value = "区域")
    private String divName;

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

    /**
     * 处理意见
     */
    @ApiModelProperty(value = "处理意见")
    private String remark;

    /**
     * 处理时间
     */
    @ApiModelProperty(value = "处理时间")
    private Instant handledOn;

}
