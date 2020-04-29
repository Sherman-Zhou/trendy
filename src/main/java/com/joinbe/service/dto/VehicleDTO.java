package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.joinbe.domain.Vehicle} entity.
 */
public class VehicleDTO implements Serializable {

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

    public String getVehicleIdNum() {
        return vehicleIdNum;
    }

    public void setVehicleIdNum(String vehicleIdNum) {
        this.vehicleIdNum = vehicleIdNum;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getTankVolume() {
        return tankVolume;
    }

    public void setTankVolume(BigDecimal tankVolume) {
        this.tankVolume = tankVolume;
    }

    public BigDecimal getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(BigDecimal fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public BigDecimal getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(BigDecimal totalMileage) {
        this.totalMileage = totalMileage;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Long getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(Long divisionId) {
        this.divisionId = divisionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VehicleDTO vehicleDTO = (VehicleDTO) o;
        if (vehicleDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), vehicleDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "VehicleDTO{" +
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
            ", divisionId=" + getDivisionId() +
            "}";
    }
}
