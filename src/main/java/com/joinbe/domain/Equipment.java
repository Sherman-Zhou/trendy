package com.joinbe.domain;

import com.joinbe.domain.enumeration.EquipmentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A Equipment.
 */
@Entity
@Table(name = "equipment")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Equipment extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 设备ID
     */
    @Size(max = 100)
    @Column(name = "identify_number", length = 100)
    @NotBlank
    private String identifyNumber;

    /**
     * 设备IMEI
     */
    @Size(max = 100)
    @NotBlank
    @Column(name = "imei", length = 100)
    private String imei;

    /**
     * 固件版本
     */
    @Size(max = 50)
    @Column(name = "version", length = 50)
    private String version;

    /**
     * SIM卡号
     */
    @Size(max = 100)
    @Column(name = "sim_card_num", length = 100)
    private String simCardNum;

    /**
     * 蓝牙名称
     */
    @Size(max = 200)
    @Column(name = "bluetooth_name", length = 200)
    private String bluetoothName;

    /**
     * 备注
     */
    @Size(max = 200)
    @Column(name = "remark", length = 200)
    private String remark;

    /**
     * 设备状态 - 枚举类型
     */
    @Column(name = "status", length = 1)
    private EquipmentStatus status;

    @Column(name = "is_online")
    private Boolean isOnline;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(unique = true)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    private Merchant merchant;
    /**
     * 初始里程数
     */
    @Column(name = "init_mileage", precision = 21, scale = 2)
    private BigDecimal initMileage;

    /**
     * 里程数倍数
     */
    @Column(name = "multiple_mileage", precision = 21, scale = 2)
    private BigDecimal multipleMileage;


    @OneToMany(mappedBy = "equipment")
//    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<VehicleTrajectory> trajectories = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifyNumber() {
        return identifyNumber;
    }

    public void setIdentifyNumber(String identifyNumber) {
        this.identifyNumber = identifyNumber;
    }

    public Equipment identifyNumber(String identifyNumber) {
        this.identifyNumber = identifyNumber;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Equipment imei(String imei) {
        this.imei = imei;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Equipment version(String version) {
        this.version = version;
        return this;
    }

    public String getSimCardNum() {
        return simCardNum;
    }

    public void setSimCardNum(String simCardNum) {
        this.simCardNum = simCardNum;
    }

    public Equipment simCardNum(String simCardNum) {
        this.simCardNum = simCardNum;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Equipment remark(String remark) {
        this.remark = remark;
        return this;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public Equipment status(EquipmentStatus status) {
        this.status = status;
        return this;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Equipment vehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public Set<VehicleTrajectory> getTrajectories() {
        return trajectories;
    }

    public void setTrajectories(Set<VehicleTrajectory> vehicleTrajectories) {
        this.trajectories = vehicleTrajectories;
    }

    public Equipment trajectories(Set<VehicleTrajectory> vehicleTrajectories) {
        this.trajectories = vehicleTrajectories;
        return this;
    }


    public Equipment addTrajectories(VehicleTrajectory vehicleTrajectory) {
        this.trajectories.add(vehicleTrajectory);
        vehicleTrajectory.setEquipment(this);
        return this;
    }

    public Equipment removeTrajectories(VehicleTrajectory vehicleTrajectory) {
        this.trajectories.remove(vehicleTrajectory);
        vehicleTrajectory.setEquipment(null);
        return this;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public BigDecimal getInitMileage() {
        return initMileage;
    }

    public void setInitMileage(BigDecimal initMileage) {
        this.initMileage = initMileage;
    }

    public BigDecimal getMultipleMileage() {
        return multipleMileage;
    }

    public void setMultipleMileage(BigDecimal multipleMileage) {
        this.multipleMileage = multipleMileage;
    }

    @Override
    public String toString() {
        return "Equipment{" +
            "id=" + id +
            ", identifyNumber='" + identifyNumber + '\'' +
            ", imei='" + imei + '\'' +
            ", version='" + version + '\'' +
            ", simCardNum='" + simCardNum + '\'' +
            ", bluetoothName='" + bluetoothName + '\'' +
            ", remark='" + remark + '\'' +
            ", status=" + status +
            ", isOnline=" + isOnline +
            ", vehicle=" + vehicle +
            ", merchant=" + merchant +
            ", initMileage=" + initMileage +
            ", multipleMileage=" + multipleMileage +
            ", trajectories=" + trajectories +
            '}';
    }
}
