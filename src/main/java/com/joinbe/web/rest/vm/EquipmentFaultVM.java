package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class EquipmentFaultVM implements Serializable {
    /**
     * 报警类型： - 枚举类型: Event
     *                     Error
     */
    @Size(max = 50)
    @ApiModelProperty(value = "报警类型：", example = "枚举类型: Event, Error")
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

    @ApiModelProperty(value = "设备ID")
    private String equipmentIdNum;

    @ApiModelProperty(value = "车辆主键")
    private Long vehicleId;

    @ApiModelProperty(value = "开始时间", example = "2020-06-30")
    private String startDate;

    @ApiModelProperty(value = "结束时间", example = "2020-06-30")
    private String endDate;


}
