package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TrajectoryReportDTO implements Serializable {


    /**
     * 接收时间
     */
    @ApiModelProperty(value = "接收时间")
    private String receivedTime;

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

    //from trajectory table
    /**
     * 轨迹ID
     */
    @Size(max = 50)
    @ApiModelProperty(value = "轨迹ID")
    private String trajectoryId;

    /**
     * 车辆点火的纬度
     */
    @Size(max = 50)
    @ApiModelProperty(value = "车辆点火的纬度")
    private String startLatitude;

    /**
     * 点火时间
     */
    @ApiModelProperty(value = "点火时间")
    private Instant startTime;

    /**
     * 熄火时间
     */
    @ApiModelProperty(value = "熄火时间")
    private Instant endTime;

    //From Equipment table
    /**
     * 设备ID
     */
    @Size(max = 100)
    private String identifyNumber;

    /**
     * 设备IMEI
     */
    @Size(max = 100)
    @NotBlank
    private String imei;


}
