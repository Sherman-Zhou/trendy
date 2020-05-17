package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A DTO for the {@link com.joinbe.domain.Vehicle} entity.
 */
@Data
public class VehicleDTO implements Serializable {

    @ApiModelProperty(value = "车辆主键")
    private Long id;

    /**
     * 车牌
     */
    @Size(max = 10)
    @ApiModelProperty(value = "车牌")
    private String licensePlateNumber;

    /**
     * 车架号
     */
    @Size(max = 50)
    @ApiModelProperty(value = "车架号")
    private String vehicleIdNum;

    /**
     * 品牌
     */
    @Size(max = 50)
    @ApiModelProperty(value = "品牌")
    private String brand;

    /**
     * 名称
     */
    @Size(max = 50)
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 年份
     */
    @Size(max = 10)
    @ApiModelProperty(value = "年份")
    private String prodYear;

    /**
     * 车型
     */
    @Size(max = 50)
    @ApiModelProperty(value = "车型")
    private String type;

    /**
     * 款式
     */
    @Size(max = 20)
    @ApiModelProperty(value = "款式")
    private String style;

    /**
     * 颜色
     */
    @Size(max = 20)
    @ApiModelProperty(value = "颜色")
    private String color;

    /**
     * 油箱容积(L)
     */
    @ApiModelProperty(value = "油箱容积(L)")
    private BigDecimal tankVolume;

    /**
     * 油耗（KM/L)
     */
    @ApiModelProperty(value = "油耗（KM/L)")
    private BigDecimal fuelConsumption;

    /**
     * 当前车辆的总里程数
     */
    @ApiModelProperty(value = "当前车辆的总里程数")
    private BigDecimal totalMileage;

    /**
     * 负责人
     */
    @Size(max = 20)
    @ApiModelProperty(value = "负责人")
    private String contactName;

    /**
     * 联系电话
     */
    @Size(max = 20)
    @ApiModelProperty(value = "联系电话")
    private String contactNumber;

    @Size(max = 1)
    private String status;

    @NotNull
    @Size(max = 20)
    private String createdBy;

    @NotNull
    private Instant createdDate;

    @Size(max = 20)
    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Long divisionId;


}
