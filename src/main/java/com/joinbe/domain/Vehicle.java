package com.joinbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.joinbe.domain.enumeration.RecordStatus;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A Vehicle.
 */
@Entity
@Table(name = "vehicle")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Vehicle extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 车牌
     */
    @Size(max = 20)
    @Column(name = "license_plate_number", length = 20)
    private String licensePlateNumber;

    /**
     * 车牌
     */
    @Size(max = 20)
    @Column(name = "license_plate_number_cn", length = 20)
    private String licensePlateNumberCn;

    /**
     * 车牌
     */
    @Size(max = 20)
    @Column(name = "license_plate_number_jp", length = 20)
    private String licensePlateNumberJp;

    /**
     * 车架号
     */
    @Size(max = 50)
    @Column(name = "vehicle_id_num", length = 50)
    private String vehicleIdNum;

    /**
     * 品牌
     */
    @Size(max = 50)
    @Column(name = "brand", length = 50)
    private String brand;

    /**
     * 品牌
     */
    @Size(max = 50)
    @Column(name = "brand_cn", length = 50)
    private String brandCn;


    /**
     * 品牌
     */
    @Size(max = 50)
    @Column(name = "brand_jp", length = 50)
    private String brandJp;


    /**
     * 名称
     */
    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    /**
     * 名称
     */
    @Size(max = 50)
    @Column(name = "name_cn", length = 50)
    private String nameCn;

    /**
     * 名称
     */
    @Size(max = 50)
    @Column(name = "name_jp", length = 50)
    private String nameJp;

    /**
     * 车型
     */
    @Size(max = 50)
    @Column(name = "type", length = 50)
    private String type;

    /**
     * 车型
     */
    @Size(max = 50)
    @Column(name = "type_cn", length = 50)
    private String typeCn;

    /**
     * 车型
     */
    @Size(max = 50)
    @Column(name = "type_jp", length = 50)
    private String typeJp;

    /**
     * 款式
     */
    @Size(max = 20)
    @Column(name = "style", length = 20)
    private String style;

    /**
     * 款式
     */
    @Size(max = 20)
    @Column(name = "style_cn", length = 20)
    private String styleCn;

    /**
     * 款式
     */
    @Size(max = 20)
    @Column(name = "style_jp", length = 20)
    private String styleJp;

    /**
     * 年份
     */
    @Size(max = 10)
    @Column(name = "prod_year", length = 10)
    private String prodYear;

    /**
     * 颜色
     */
    @Size(max = 20)
    @Column(name = "color", length = 20)
    private String color;

    /**
     * 油箱容积(L)
     */
    @Column(name = "tank_volume", precision = 21, scale = 2)
    private BigDecimal tankVolume;

    /**
     * 油耗（KM/L)
     */
    @Column(name = "fuel_consumption", precision = 21, scale = 2)
    private BigDecimal fuelConsumption;

    /**
     * 当前车辆的总里程数
     */
    @Column(name = "total_mileage", precision = 21, scale = 2)
    private BigDecimal totalMileage;

    /**
     * 负责人
     */
    @Size(max = 20)
    @Column(name = "contact_name", length = 20)
    private String contactName;

    /**
     * 负责人
     */
    @Size(max = 20)
    @Column(name = "contact_name_cn", length = 20)
    private String contactNameCn;

    /**
     * 负责人
     */
    @Size(max = 20)
    @Column(name = "contact_name_jp", length = 20)
    private String contactNameJp;

    /**
     * 联系电话
     */
    @Size(max = 20)
    @Column(name = "contact_number", length = 20)
    private String contactNumber;


    @Column(name = "status", length = 1)
    private RecordStatus status;

    @Column(name = "bounded")
    private Boolean bounded;

    @Column(name = "is_moving")
    private Boolean isMoving;

    @OneToOne(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private Equipment equipment;


    @OneToMany(mappedBy = "vehicle")
//    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<VehicleTrajectory> trajectories = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("shops")
    private Shop shop;

    @ManyToOne
    @JsonIgnoreProperties("city")
    private City city;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }

    public void setLicensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
    }

    public Vehicle licensePlateNumber(String licensePlateNumber) {
        this.licensePlateNumber = licensePlateNumber;
        return this;
    }

    public String getVehicleIdNum() {
        return vehicleIdNum;
    }

    public void setVehicleIdNum(String vehicleIdNum) {
        this.vehicleIdNum = vehicleIdNum;
    }

    public Vehicle vehicleIdNum(String vehicleIdNum) {
        this.vehicleIdNum = vehicleIdNum;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Vehicle brand(String brand) {
        this.brand = brand;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Vehicle type(String type) {
        this.type = type;
        return this;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Vehicle style(String style) {
        this.style = style;
        return this;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Vehicle color(String color) {
        this.color = color;
        return this;
    }

    public String getProdYear() {
        return prodYear;
    }

    public void setProdYear(String prodYear) {
        this.prodYear = prodYear;
    }

    public BigDecimal getTankVolume() {
        return tankVolume;
    }

    public void setTankVolume(BigDecimal tankVolume) {
        this.tankVolume = tankVolume;
    }

    public Vehicle tankVolume(BigDecimal tankVolume) {
        this.tankVolume = tankVolume;
        return this;
    }

    public BigDecimal getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(BigDecimal fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public Vehicle fuelConsumption(BigDecimal fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
        return this;
    }

    public BigDecimal getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(BigDecimal totalMileage) {
        this.totalMileage = totalMileage;
    }

    public Vehicle totalMileage(BigDecimal totalMileage) {
        this.totalMileage = totalMileage;
        return this;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Vehicle contactName(String contactName) {
        this.contactName = contactName;
        return this;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Vehicle contactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
        return this;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Vehicle status(RecordStatus status) {
        this.status = status;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<VehicleTrajectory> getTrajectories() {
        return trajectories;
    }

    public void setTrajectories(Set<VehicleTrajectory> vehicleTrajectories) {
        this.trajectories = vehicleTrajectories;
    }

    public Vehicle trajectories(Set<VehicleTrajectory> vehicleTrajectories) {
        this.trajectories = vehicleTrajectories;
        return this;
    }

    public Vehicle addTrajectories(VehicleTrajectory vehicleTrajectory) {
        this.trajectories.add(vehicleTrajectory);
        vehicleTrajectory.setVehicle(this);
        return this;
    }

    public Vehicle removeTrajectories(VehicleTrajectory vehicleTrajectory) {
        this.trajectories.remove(vehicleTrajectory);
        vehicleTrajectory.setVehicle(null);
        return this;
    }


    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getLicensePlateNumberCn() {
        return licensePlateNumberCn;
    }

    public void setLicensePlateNumberCn(String licensePlateNumberCn) {
        this.licensePlateNumberCn = licensePlateNumberCn;
    }

    public String getLicensePlateNumberJp() {
        return licensePlateNumberJp;
    }

    public void setLicensePlateNumberJp(String licensePlateNumberJp) {
        this.licensePlateNumberJp = licensePlateNumberJp;
    }

    public String getBrandCn() {
        return brandCn;
    }

    public void setBrandCn(String brandCn) {
        this.brandCn = brandCn;
    }

    public String getBrandJp() {
        return brandJp;
    }

    public void setBrandJp(String brandJp) {
        this.brandJp = brandJp;
    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public String getNameJp() {
        return nameJp;
    }

    public void setNameJp(String nameJp) {
        this.nameJp = nameJp;
    }

    public String getTypeCn() {
        return typeCn;
    }

    public void setTypeCn(String typeCn) {
        this.typeCn = typeCn;
    }

    public String getTypeJp() {
        return typeJp;
    }

    public void setTypeJp(String typeJp) {
        this.typeJp = typeJp;
    }

    public String getStyleCn() {
        return styleCn;
    }

    public void setStyleCn(String styleCn) {
        this.styleCn = styleCn;
    }

    public String getStyleJp() {
        return styleJp;
    }

    public void setStyleJp(String styleJp) {
        this.styleJp = styleJp;
    }

    public String getContactNameCn() {
        return contactNameCn;
    }

    public void setContactNameCn(String contactNameCn) {
        this.contactNameCn = contactNameCn;
    }

    public String getContactNameJp() {
        return contactNameJp;
    }

    public void setContactNameJp(String contactNameJp) {
        this.contactNameJp = contactNameJp;
    }

    public Boolean getMoving() {
        return isMoving;
    }

    public void setMoving(Boolean moving) {
        isMoving = moving;
    }

    public Boolean getIsMoving() {
        return isMoving;
    }

    public void setIsMoving(Boolean moving) {
        isMoving = moving;
    }

    public Boolean getBounded() {
        return bounded;
    }

    public void setBounded(Boolean bounded) {
        this.bounded = bounded;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
            "id=" + getId() +
            ", licensePlateNumber='" + getLicensePlateNumber() + "'" +
            ", vehicleIdNum='" + getVehicleIdNum() + "'" +
            ", brand='" + getBrand() + "'" +
            ", type='" + getType() + "'" +
            ", style='" + getStyle() + "'" +
            ", color='" + getColor() + "'" +
            ", tankVolume=" + getTankVolume() +
            ", fuelConsumption=" + getFuelConsumption() +
            ", totalMileage=" + getTotalMileage() +
            ", contactName='" + getContactName() + "'" +
            ", contactNumber='" + getContactNumber() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
