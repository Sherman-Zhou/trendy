package com.joinbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * A VehicleTrajectoryDetails.
 */
@Entity
@Table(name = "vehicle_trajectory_details")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class VehicleTrajectoryDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 接收时间
     */
    @Column(name = "received_time")
    private Instant receivedTime;

    /**
     * 经度
     */
    @Column(name = "longitude", precision = 21, scale = 7)
    private BigDecimal longitude;

    /**
     * 维度
     */
    @Column(name = "latitude", precision = 21, scale = 7)
    private BigDecimal latitude;

    /**
     * 实际速度
     */
    @Column(name = "actual_speed", precision = 21, scale = 2)
    private BigDecimal actualSpeed;

    /**
     * 限定速度
     */
    @Column(name = "limited_speed", precision = 21, scale = 2)
    private BigDecimal limitedSpeed;

    /**
     * 胎压(左前)
     */
    @Column(name = "tire_pressure_front_left", precision = 21, scale = 2)
    private BigDecimal tirePressureFrontLeft;

    /**
     * 胎压(右前)
     */
    @Column(name = "tire_pressure_front_right", precision = 21, scale = 2)
    private BigDecimal tirePressureFrontRight;

    /**
     * 胎压(左后)
     */
    @Column(name = "tire_pressure_rear_left", precision = 21, scale = 2)
    private BigDecimal tirePressureRearLeft;

    /**
     * 胎压(右后)
     */
    @Column(name = "tire_pressure_rear_right", precision = 21, scale = 2)
    private BigDecimal tirePressureRearRight;

    @ManyToOne
    @JsonIgnoreProperties("details")
    private VehicleTrajectory vehicleTrajectory;

    @Column(name = "mileage", precision = 21, scale = 2)
    private BigDecimal mileage;

    @Column(name = "voltage_input2", precision = 21, scale = 2)
    private BigDecimal voltageInput2;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
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

    public VehicleTrajectoryDetails receivedTime(Instant receivedTime) {
        this.receivedTime = receivedTime;
        return this;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public VehicleTrajectoryDetails longitude(BigDecimal longitude) {
        this.longitude = longitude;
        return this;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public VehicleTrajectoryDetails latitude(BigDecimal latitude) {
        this.latitude = latitude;
        return this;
    }

    public BigDecimal getActualSpeed() {
        return actualSpeed;
    }

    public void setActualSpeed(BigDecimal actualSpeed) {
        this.actualSpeed = actualSpeed;
    }

    public VehicleTrajectoryDetails actualSpeed(BigDecimal actualSpeed) {
        this.actualSpeed = actualSpeed;
        return this;
    }

    public BigDecimal getLimitedSpeed() {
        return limitedSpeed;
    }

    public void setLimitedSpeed(BigDecimal limitedSpeed) {
        this.limitedSpeed = limitedSpeed;
    }

    public VehicleTrajectoryDetails limitedSpeed(BigDecimal limitedSpeed) {
        this.limitedSpeed = limitedSpeed;
        return this;
    }

    public BigDecimal getTirePressureFrontLeft() {
        return tirePressureFrontLeft;
    }

    public void setTirePressureFrontLeft(BigDecimal tirePressureFrontLeft) {
        this.tirePressureFrontLeft = tirePressureFrontLeft;
    }

    public VehicleTrajectoryDetails tirePressureFrontLeft(BigDecimal tirePressureFrontLeft) {
        this.tirePressureFrontLeft = tirePressureFrontLeft;
        return this;
    }

    public BigDecimal getTirePressureFrontRight() {
        return tirePressureFrontRight;
    }

    public void setTirePressureFrontRight(BigDecimal tirePressureFrontRight) {
        this.tirePressureFrontRight = tirePressureFrontRight;
    }

    public VehicleTrajectoryDetails tirePressureFrontRight(BigDecimal tirePressureFrontRight) {
        this.tirePressureFrontRight = tirePressureFrontRight;
        return this;
    }

    public BigDecimal getTirePressureRearLeft() {
        return tirePressureRearLeft;
    }

    public void setTirePressureRearLeft(BigDecimal tirePressureRearLeft) {
        this.tirePressureRearLeft = tirePressureRearLeft;
    }

    public VehicleTrajectoryDetails tirePressureRearLeft(BigDecimal tirePressureRearLeft) {
        this.tirePressureRearLeft = tirePressureRearLeft;
        return this;
    }

    public BigDecimal getTirePressureRearRight() {
        return tirePressureRearRight;
    }

    public void setTirePressureRearRight(BigDecimal tirePressureRearRight) {
        this.tirePressureRearRight = tirePressureRearRight;
    }

    public VehicleTrajectoryDetails tirePressureRearRight(BigDecimal tirePressureRearRight) {
        this.tirePressureRearRight = tirePressureRearRight;
        return this;
    }

    public VehicleTrajectory getVehicleTrajectory() {
        return vehicleTrajectory;
    }

    public void setVehicleTrajectory(VehicleTrajectory vehicleTrajectory) {
        this.vehicleTrajectory = vehicleTrajectory;
    }

    public VehicleTrajectoryDetails vehicleTrajectory(VehicleTrajectory vehicleTrajectory) {
        this.vehicleTrajectory = vehicleTrajectory;
        return this;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public BigDecimal getVoltageInput2() {
        return voltageInput2;
    }

    public void setVoltageInput2(BigDecimal voltageInput2) {
        this.voltageInput2 = voltageInput2;
    }

    @Override
    public String toString() {
        return "VehicleTrajectoryDetails{" +
            "id=" + id +
            ", receivedTime=" + receivedTime +
            ", longitude=" + longitude +
            ", latitude=" + latitude +
            ", actualSpeed=" + actualSpeed +
            ", limitedSpeed=" + limitedSpeed +
            ", tirePressureFrontLeft=" + tirePressureFrontLeft +
            ", tirePressureFrontRight=" + tirePressureFrontRight +
            ", tirePressureRearLeft=" + tirePressureRearLeft +
            ", tirePressureRearRight=" + tirePressureRearRight +
            ", vehicleTrajectory=" + vehicleTrajectory +
            ", mileage=" + mileage +
            '}';
    }
}
