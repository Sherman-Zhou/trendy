package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

public class TrajectoryResponseResult {
    /**
     * 接收时间
     */
    @ApiModelProperty(value = "接收时间, 时间戳")
    private Long receivedTime;


    @ApiModelProperty(value = "经度")
    private BigDecimal longitude;

    /**
     * 维度
     */
    @ApiModelProperty(value = "维度")
    private BigDecimal latitude;

    /**
     * 实际速度
     */
    @ApiModelProperty(value = "实际速度")
    private BigDecimal actualSpeed;

    /**
     * 里程数
     */
    @ApiModelProperty(value = "里程数")
    private BigDecimal mileage;

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

    @ApiModelProperty(value = "轨迹ID")
    private String trajectoryId;


    public String getTrajectoryId() {
        return trajectoryId;
    }

    public void setTrajectoryId(String trajectoryId) {
        this.trajectoryId = trajectoryId;
    }

    public Long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getActualSpeed() {
        return actualSpeed;
    }

    public void setActualSpeed(BigDecimal actualSpeed) {
        this.actualSpeed = actualSpeed;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public BigDecimal getLimitedSpeed() {
        return limitedSpeed;
    }

    public void setLimitedSpeed(BigDecimal limitedSpeed) {
        this.limitedSpeed = limitedSpeed;
    }

    public BigDecimal getTirePressureFrontLeft() {
        return tirePressureFrontLeft;
    }

    public void setTirePressureFrontLeft(BigDecimal tirePressureFrontLeft) {
        this.tirePressureFrontLeft = tirePressureFrontLeft;
    }

    public BigDecimal getTirePressureFrontRight() {
        return tirePressureFrontRight;
    }

    public void setTirePressureFrontRight(BigDecimal tirePressureFrontRight) {
        this.tirePressureFrontRight = tirePressureFrontRight;
    }

    public BigDecimal getTirePressureRearLeft() {
        return tirePressureRearLeft;
    }

    public void setTirePressureRearLeft(BigDecimal tirePressureRearLeft) {
        this.tirePressureRearLeft = tirePressureRearLeft;
    }

    public BigDecimal getTirePressureRearRight() {
        return tirePressureRearRight;
    }

    public void setTirePressureRearRight(BigDecimal tirePressureRearRight) {
        this.tirePressureRearRight = tirePressureRearRight;
    }

    @Override
    public String toString() {
        return "TrajectoryResponseResult{" +
            "receivedTime=" + receivedTime +
            ", longitude=" + longitude +
            ", latitude=" + latitude +
            ", actualSpeed=" + actualSpeed +
            ", mileage=" + mileage +
            ", limitedSpeed=" + limitedSpeed +
            ", tirePressureFrontLeft=" + tirePressureFrontLeft +
            ", tirePressureFrontRight=" + tirePressureFrontRight +
            ", tirePressureRearLeft=" + tirePressureRearLeft +
            ", tirePressureRearRight=" + tirePressureRearRight +
            ", trajectoryId='" + trajectoryId + '\'' +
            '}';
    }
}
