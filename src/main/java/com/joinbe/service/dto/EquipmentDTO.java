package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

/**
 * A DTO for the {@link com.joinbe.domain.Equipment} entity.
 */
@Data
public class EquipmentDTO implements Serializable {

    @ApiModelProperty(value = "设备主键")
    private Long id;

    /**
     * 设备ID
     */
    @Size(max = 100)
    @ApiModelProperty(value = "设备ID")
    private String identifyNumber;

    /**
     * 设备IMEI
     */
    @Size(max = 100)
    @ApiModelProperty(value = "设备IMEI")
    private String imei;

    /**
     * 固件版本
     */
    @Size(max = 50)
    @ApiModelProperty(value = "固件版本")
    private String version;

    /**
     * SIM卡号
     */
    @Size(max = 100)
    @ApiModelProperty(value = "SIM卡号")
    private String simCardNum;

    /**
     * 备注
     */
    @Size(max = 200)
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 设备状态 - 枚举类型\n绑定\n未绑定\"
     */
    @Size(max = 1)
    @ApiModelProperty(value = "设备状态 - 枚举类型- B:绑定 U:未绑定 D: 已删除", hidden = true)
    @Pattern(regexp = "[BUD]")
    private String status;


    @Size(max = 20)
    @ApiModelProperty(value = "创建者",hidden = true )
    private String createdBy;

    @ApiModelProperty(value = "创建时间",hidden = true )
    private Instant createdDate;

    @Size(max = 20)
    @ApiModelProperty(value = "更新者",hidden = true )
    private String lastModifiedBy;

    @ApiModelProperty(value = "更新时间",hidden = true )
    private Instant lastModifiedDate;

}
