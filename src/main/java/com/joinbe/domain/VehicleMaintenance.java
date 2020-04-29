package com.joinbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * A VehicleMaintenance.
 */
@Entity
//@Table(name = "vehicle_maintenance")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class VehicleMaintenance extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 当前油量(L)
     */
    @Column(name = "fuel", precision = 21, scale = 2)
    private BigDecimal fuel;

    /**
     * 当前里程數(KM)
     */
    @Column(name = "mileage", precision = 21, scale = 2)
    private BigDecimal mileage;

    /**
     * 备注
     */
    @Size(max = 200)
    @Column(name = "remark", length = 200)
    private String remark;


    @ManyToOne
    @JsonIgnoreProperties("vehicleMaintenance")
    private Vehicle vehicle;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getFuel() {
        return fuel;
    }

    public void setFuel(BigDecimal fuel) {
        this.fuel = fuel;
    }

    public VehicleMaintenance fuel(BigDecimal fuel) {
        this.fuel = fuel;
        return this;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public VehicleMaintenance mileage(BigDecimal mileage) {
        this.mileage = mileage;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public VehicleMaintenance remark(String remark) {
        this.remark = remark;
        return this;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public VehicleMaintenance vehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    @Override
    public String toString() {
        return "VehicleMaintenance{" +
            "id=" + getId() +
            ", fuel=" + getFuel() +
            ", mileage=" + getMileage() +
            ", remark='" + getRemark() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
