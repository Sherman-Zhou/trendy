package com.joinbe.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;

/**
 * A EquipmentFault.
 */
@Entity
@Table(name = "equipment_fault")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EquipmentFault extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 报警类型： - 枚举类型
     * Event
     * Error
     */
    @Size(max = 50)
    @Column(name = "alert_type", length = 50)
    private String alertType;

    /**
     * 报警内容：
     * Main power low event
     * Device Lost
     */
    @Size(max = 200)
    @Column(name = "alert_content", length = 200)
    private String alertContent;

    /**
     * 是否已读
     */
    @Column(name = "is_read")
    private Boolean isRead;

    /**
     * 处理意见
     */
    @Size(max = 200)
    @Column(name = "remark", length = 200)
    private String remark;

    /**
     * 处理时间
     */
    @Column(name = "handledOn")
    private Instant handledOn;


    @ManyToOne
    @JsonIgnoreProperties("equipmentFaults")
    private Vehicle vehicle;

    @ManyToOne
    @JsonIgnoreProperties("equipmentFaults")
    private Equipment equipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public EquipmentFault alertType(String alertType) {
        this.alertType = alertType;
        return this;
    }

    public String getAlertContent() {
        return alertContent;
    }

    public void setAlertContent(String alertContent) {
        this.alertContent = alertContent;
    }

    public EquipmentFault alertContent(String alertContent) {
        this.alertContent = alertContent;
        return this;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public EquipmentFault isRead(Boolean isRead) {
        this.isRead = isRead;
        return this;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Instant getHandledOn() {
        return handledOn;
    }

    public void setHandledOn(Instant handledOn) {
        this.handledOn = handledOn;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public EquipmentFault vehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public EquipmentFault equipment(Equipment equipment) {
        this.equipment = equipment;
        return this;
    }

    @Override
    public String toString() {
        return "EquipmentFault{" +
            "id=" + getId() +
            ", alertType='" + getAlertType() + "'" +
            ", alertContent='" + getAlertContent() + "'" +
            ", isRead='" + getIsRead() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
