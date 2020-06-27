package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.joinbe.domain.VehicleTrajectory} entity.
 */
public class VehicleTrajectoryDTO implements Serializable {

    private Long id;

    /**
     * 轨迹ID
     */
    @Size(max = 50)
    @ApiModelProperty(value = "轨迹ID")
    private String trajectoryId;

    /**
     * 车辆点火的经度
     */
    @Size(max = 50)
    @ApiModelProperty(value = "车辆点火的经度")
    private String startLongitude;

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

    /**
     * 轨迹的里程数(KM)
     */
    @ApiModelProperty(value = "轨迹的里程数(KM)")
    private BigDecimal mileage;

    /**
     * 超速次数
     */
    @ApiModelProperty(value = "超速次数")
    private Integer overspeedNum;

    /**
     * 结算状态 - 枚举类型\n已結算\n未结算
     */
    @Size(max = 1)
    @ApiModelProperty(value = "结算状态 - 枚举类型\nS:已結算\nU:未结算")
    private String status;


    private Long vehicleId;

    private Long equipmentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrajectoryId() {
        return trajectoryId;
    }

    public void setTrajectoryId(String trajectoryId) {
        this.trajectoryId = trajectoryId;
    }

    public String getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(String startLongitude) {
        this.startLongitude = startLongitude;
    }

    public String getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(String startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public Integer getOverspeedNum() {
        return overspeedNum;
    }

    public void setOverspeedNum(Integer overspeedNum) {
        this.overspeedNum = overspeedNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VehicleTrajectoryDTO vehicleTrajectoryDTO = (VehicleTrajectoryDTO) o;
        if (vehicleTrajectoryDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), vehicleTrajectoryDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "VehicleTrajectoryDTO{" +
            "id=" + getId() +
            ", trajectoryId='" + getTrajectoryId() + "'" +
            ", startLongitude='" + getStartLongitude() + "'" +
            ", startLatitude='" + getStartLatitude() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", mileage=" + getMileage() +
            ", overspeedNum=" + getOverspeedNum() +
            ", status='" + getStatus() + "'" +
            ", vehicleId=" + getVehicleId() +
            ", equipmentId=" + getEquipmentId() +
            "}";
    }
}
