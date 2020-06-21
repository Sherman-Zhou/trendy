package com.joinbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.joinbe.domain.enumeration.EventCategory;
import com.joinbe.domain.enumeration.EventType;
import com.joinbe.domain.enumeration.OperationResult;
import com.joinbe.domain.enumeration.OperationSourceType;

import javax.persistence.*;

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

    /**
     * 操作来源：- 枚举类型
     * 控制端
     * APP蓝牙
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_source_type", length = 50)
    private OperationSourceType operationSourceType;

    /**
     * 事件类型：- 枚举类型
     * 蓝牙密钥
     * 开关锁
     * 绑定/解绑
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 50)
    private EventCategory eventType;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "event_desc", length = 50)
    private EventType eventDesc;

    /**
     * 结果: - 枚举类型
     * 成功
     * 失败
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "result", length = 50)
    private OperationResult result;


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


    public OperationSourceType getOperationSourceType() {
        return operationSourceType;
    }

    public void setOperationSourceType(OperationSourceType operationSourceType) {
        this.operationSourceType = operationSourceType;
    }

    public EquipmentOperationRecord operationSourceType(OperationSourceType operationSourceType) {
        this.operationSourceType = operationSourceType;
        return this;
    }

    public EventCategory getEventType() {
        return eventType;
    }

    public void setEventType(EventCategory eventType) {
        this.eventType = eventType;
    }

    public EquipmentOperationRecord eventType(EventCategory eventType) {
        this.eventType = eventType;
        return this;
    }

    public EventType getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(EventType eventDesc) {
        this.eventDesc = eventDesc;
    }

    public EquipmentOperationRecord eventDesc(EventType eventDesc) {
        this.eventDesc = eventDesc;
        return this;
    }

    public OperationResult getResult() {
        return result;
    }

    public void setResult(OperationResult result) {
        this.result = result;
    }

    public EquipmentOperationRecord result(OperationResult result) {
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
