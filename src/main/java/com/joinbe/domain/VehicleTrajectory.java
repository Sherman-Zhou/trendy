package com.joinbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A VehicleTrajectory.
 */
@Entity
@Table(name = "vehicle_trajectory")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class VehicleTrajectory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 轨迹ID
     */
    @Size(max = 50)
    @Column(name = "trajectory_id", length = 50)
    private String trajectoryId;

    /**
     * 车辆点火的经度
     */
    @Column(name = "start_longitude", precision = 21, scale = 7)
    private BigDecimal startLongitude;

    /**
     * 车辆点火的纬度
     */
    @Column(name = "start_latitude", precision = 21, scale = 7)
    private BigDecimal startLatitude;

    /**
     * 点火时间
     */
    @Column(name = "start_time")
    private Instant startTime;

    /**
     * 熄火时间
     */
    @Column(name = "end_time")
    private Instant endTime;

    /**
     * 轨迹的里程数(KM)
     */
    @Column(name = "mileage", precision = 21, scale = 2)
    private BigDecimal mileage;

    /**
     * 超速次数
     */
    @Column(name = "overspeed_num")
    private Integer overspeedNum;

    /**
     * 结算状态 - 枚举类型
     * 已結算
     * 未结算
     */
    @Size(max = 1)
    @Column(name = "status", length = 1)
    private String status;

    @OneToMany(mappedBy = "vehicleTrajectory" ,cascade=CascadeType.ALL)
//    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<VehicleTrajectoryDetails> details = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("trajectories")
    private Vehicle vehicle;

    @ManyToOne
    @JsonIgnoreProperties("trajectories")
    private Equipment equipment;

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

    public VehicleTrajectory trajectoryId(String trajectoryId) {
        this.trajectoryId = trajectoryId;
        return this;
    }

    public BigDecimal getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(BigDecimal startLongitude) {
        this.startLongitude = startLongitude;
    }

    public VehicleTrajectory startLongitude(BigDecimal startLongitude) {
        this.startLongitude = startLongitude;
        return this;
    }

    public BigDecimal getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(BigDecimal startLatitude) {
        this.startLatitude = startLatitude;
    }

    public VehicleTrajectory startLatitude(BigDecimal startLatitude) {
        this.startLatitude = startLatitude;
        return this;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public VehicleTrajectory startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public VehicleTrajectory endTime(Instant endTime) {
        this.endTime = endTime;
        return this;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public VehicleTrajectory mileage(BigDecimal mileage) {
        this.mileage = mileage;
        return this;
    }

    public Integer getOverspeedNum() {
        return overspeedNum;
    }

    public void setOverspeedNum(Integer overspeedNum) {
        this.overspeedNum = overspeedNum;
    }

    public VehicleTrajectory overspeedNum(Integer overspeedNum) {
        this.overspeedNum = overspeedNum;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VehicleTrajectory status(String status) {
        this.status = status;
        return this;
    }

    public Set<VehicleTrajectoryDetails> getDetails() {
        return details;
    }

    public void setDetails(Set<VehicleTrajectoryDetails> vehicleTrajectoryDetails) {
        this.details = vehicleTrajectoryDetails;
    }

    public VehicleTrajectory details(Set<VehicleTrajectoryDetails> vehicleTrajectoryDetails) {
        this.details = vehicleTrajectoryDetails;
        return this;
    }

    public VehicleTrajectory addDetails(VehicleTrajectoryDetails vehicleTrajectoryDetails) {
        this.details.add(vehicleTrajectoryDetails);
        vehicleTrajectoryDetails.setVehicleTrajectory(this);
        return this;
    }

    public VehicleTrajectory removeDetails(VehicleTrajectoryDetails vehicleTrajectoryDetails) {
        this.details.remove(vehicleTrajectoryDetails);
        vehicleTrajectoryDetails.setVehicleTrajectory(null);
        return this;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public VehicleTrajectory vehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public VehicleTrajectory equipment(Equipment equipment) {
        this.equipment = equipment;
        return this;
    }

    @Override
    public String toString() {
        return "VehicleTrajectory{" +
            "id=" + getId() +
            ", trajectoryId='" + getTrajectoryId() + "'" +
            ", startLongitude='" + getStartLongitude() + "'" +
            ", startLatitude='" + getStartLatitude() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", mileage=" + getMileage() +
            ", overspeedNum=" + getOverspeedNum() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
