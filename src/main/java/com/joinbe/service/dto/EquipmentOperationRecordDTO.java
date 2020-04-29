package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.joinbe.domain.EquipmentOperationRecord} entity.
 */
public class EquipmentOperationRecordDTO implements Serializable {

    private Long id;

    @Size(max = 1)
    private String status;

    /**
     * 操作来源：- 枚举类型\n控制端\nAPP蓝牙
     */
    @Size(max = 50)
    @ApiModelProperty(value = "操作来源：- 枚举类型\n控制端\nAPP蓝牙")
    private String operationSourceType;

    /**
     * 事件类型：- 枚举类型\n蓝牙密钥\n开关锁\n绑定/解绑
     */
    @Size(max = 50)
    @ApiModelProperty(value = "事件类型：- 枚举类型\n蓝牙密钥\n开关锁\n绑定/解绑")
    private String eventType;

    /**
     * 事件描述：（6种）- 枚举类型\n发放密钥\n收回密钥\n开锁\n关锁\n绑定设备\n解绑设备\"
     */
    @Size(max = 50)
    @ApiModelProperty(value = "事件描述：（6种）- 枚举类型\n发放密钥\n收回密钥\n开锁\n关锁\n绑定设备\n解绑设备\"")
    private String eventDesc;

    /**
     * 结果: - 枚举类型\n成功\n失败
     */
    @Size(max = 50)
    @ApiModelProperty(value = "结果: - 枚举类型\n成功\n失败")
    private String result;

    @NotNull
    @Size(max = 20)
    private String createdBy;

    @NotNull
    private Instant createdDate;

    @Size(max = 20)
    private String lastModifiedBy;

    private Instant lastModifiedDate;


    private Long vehicleId;

    private Long equipmentId;

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

    public String getOperationSourceType() {
        return operationSourceType;
    }

    public void setOperationSourceType(String operationSourceType) {
        this.operationSourceType = operationSourceType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

        EquipmentOperationRecordDTO equipmentOperationRecordDTO = (EquipmentOperationRecordDTO) o;
        if (equipmentOperationRecordDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), equipmentOperationRecordDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EquipmentOperationRecordDTO{" +
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
            ", vehicleId=" + getVehicleId() +
            ", equipmentId=" + getEquipmentId() +
            "}";
    }
}
