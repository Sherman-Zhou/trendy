package com.joinbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * A EquipmentOperationRecord.
 */
@Entity
@Table(name = "equipment_operation_record")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EquipmentOperationRecord extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 1)
    @Column(name = "status", length = 1)
    private String status;

    /**
     * 操作来源：- 枚举类型
     * 控制端
     * APP蓝牙
     */
    @Size(max = 50)
    @Column(name = "operation_source_type", length = 50)
    private String operationSourceType;

    /**
     * 事件类型：- 枚举类型
     * 蓝牙密钥
     * 开关锁
     * 绑定/解绑
     */
    @Size(max = 50)
    @Column(name = "event_type", length = 50)
    private String eventType;

    /**
     * 事件描述：（6种）-
     * 枚举类型
     * 发放密钥
     * 收回密钥
     * 开锁
     * 关锁
     * 绑定设备
     * 解绑设备     *
     */
    @Size(max = 50)
    @Column(name = "event_desc", length = 50)
    private String eventDesc;

    /**
     * 结果: - 枚举类型
     * 成功
     * 失败
     */
    @Size(max = 50)
    @Column(name = "result", length = 50)
    private String result;


    @ManyToOne
    @JsonIgnoreProperties("equipmentOperationRecords")
    private Vehicle vehicle;

    @ManyToOne
    @JsonIgnoreProperties("equipmentOperationRecords")
    private Equipment equipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EquipmentOperationRecord status(String status) {
        this.status = status;
        return this;
    }

    public String getOperationSourceType() {
        return operationSourceType;
    }

    public void setOperationSourceType(String operationSourceType) {
        this.operationSourceType = operationSourceType;
    }

    public EquipmentOperationRecord operationSourceType(String operationSourceType) {
        this.operationSourceType = operationSourceType;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public EquipmentOperationRecord eventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public EquipmentOperationRecord eventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
        return this;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public EquipmentOperationRecord result(String result) {
        this.result = result;
        return this;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public EquipmentOperationRecord vehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public EquipmentOperationRecord equipment(Equipment equipment) {
        this.equipment = equipment;
        return this;
    }

    @Override
    public String toString() {
        return "EquipmentOperationRecord{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", operationSourceType='" + getOperationSourceType() + "'" +
            ", eventType='" + getEventType() + "'" +
            ", eventDesc='" + getEventDesc() + "'" +
            ", result='" + getResult() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
