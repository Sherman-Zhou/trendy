package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;


@Data
public class VehicleTrajectoryDetailsDTO implements Serializable {

    private Long id;

    /**
     * 接收时间
     */
    @ApiModelProperty(value = "接收时间")
    private Instant receivedTime;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    @Size(max = 50)
    private BigDecimal lng;

    /**
     * 维度
     */
    @Size(max = 50)
    @ApiModelProperty(value = "维度")
    private BigDecimal lat;

    /**
     * 实际速度
     */
    @ApiModelProperty(value = "实际速度")
    private BigDecimal actualSpeed;

    /**
     * 限定速度
     */
    @ApiModelProperty(value = "限定速度")
    private BigDecimal limitedSpeed;

    /**
     * 胎压(左前)
     */
    @ApiModelProperty(value = "胎压(左前)")
    private BigDecimal tirePressureFrontLeft;

    /**
     * 胎压(右前)
     */
    @ApiModelProperty(value = "胎压(右前)")
    private BigDecimal tirePressureFrontRight;

    /**
     * 胎压(左后)
     */
    @ApiModelProperty(value = "胎压(左后)")
    private BigDecimal tirePressureRearLeft;

    /**
     * 胎压(右后)
     */
    @ApiModelProperty(value = "胎压(右后)")
    private BigDecimal tirePressureRearRight;


    private Long vehicleTrajectoryId;

}
