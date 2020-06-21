package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.joinbe.domain.VehicleTrajectoryDetails} entity.
 */
public class VehicleTrajectoryDetailsDTO implements Serializable {

    private Long id;

    /**
     * 接收时间
     */
    @ApiModelProperty(value = "接收时间")
    private Instant receivedTime;

    @Size(max = 50)
    private BigDecimal longitude;

    /**
     * 维度
     */
    @Size(max = 50)
    @ApiModelProperty(value = "维度")
    private BigDecimal latitude;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Instant receivedTime) {
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

    public Long getVehicleTrajectoryId() {
        return vehicleTrajectoryId;
    }

    public void setVehicleTrajectoryId(Long vehicleTrajectoryId) {
        this.vehicleTrajectoryId = vehicleTrajectoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VehicleTrajectoryDetailsDTO vehicleTrajectoryDetailsDTO = (VehicleTrajectoryDetailsDTO) o;
        if (vehicleTrajectoryDetailsDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), vehicleTrajectoryDetailsDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "VehicleTrajectoryDetailsDTO{" +
            "id=" + getId() +
            ", receivedTime='" + getReceivedTime() + "'" +
            ", longitude='" + getLongitude() + "'" +
            ", latitude='" + getLatitude() + "'" +
            ", actualSpeed=" + getActualSpeed() +
            ", limitedSpeed=" + getLimitedSpeed() +
            ", tirePressureFrontLeft=" + getTirePressureFrontLeft() +
            ", tirePressureFrontRight=" + getTirePressureFrontRight() +
            ", tirePressureRearLeft=" + getTirePressureRearLeft() +
            ", tirePressureRearRight=" + getTirePressureRearRight() +
            ", vehicleTrajectoryId=" + getVehicleTrajectoryId() +
            "}";
    }
}
