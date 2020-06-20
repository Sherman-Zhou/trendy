package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Data
public class EquipmentOperationRecordDTO implements Serializable {

    private Long id;

    @Size(max = 1)
    private String status;

    /**
     * 操作来源：- 枚举类型\n控制端\nAPP蓝牙
     */
    @Size(max = 50)
    @ApiModelProperty(value = "操作来源：- 枚举类型\nPLATFORM: 控制端\nAPP: APP蓝牙")
    private String operationSourceType;

    /**
     * 事件类型：- 枚举类型\n蓝牙密钥\n开关锁\n绑定/解绑
     */
    @Size(max = 50)
    @ApiModelProperty(value = "事件类型：- 枚举类型: BLUETOOTH: 蓝牙密钥, LOCK:开关锁: BINDING:绑定/解绑")
    private String eventType;

    /**
     * 事件描述：（6种）- 枚举类型- RELEASE:发放密钥, REVOKE: 收回密钥, LOCK:开锁, UNLOCK:关锁, BINDING: 绑定设备, UNBINDING:解绑设备"
     */
    @Size(max = 50)
    @ApiModelProperty(value = "事件描述: 枚举类型- RELEASE:发放密钥, REVOKE: 收回密钥, LOCK:开锁, UNLOCK:关锁, BINDING: 绑定设备, UNBINDING:解绑设备")
    private String eventDesc;

    /**
     * 结果: - 枚举类型\n成功\n失败
     */
    @Size(max = 50)
    @ApiModelProperty(value = "结果: - 枚举类型 SUCCESS:成功 FAILURE: 失败")
    private String result;


    @ApiModelProperty(value = "设备ID")
    private String identifyNumber;

    @Size(max = 10)
    @ApiModelProperty(value = "车牌")
    private String licensePlateNumber;

    @ApiModelProperty(value = "组织")
    private String orgName;

    @ApiModelProperty(value = "区域")
    private String divName;

    @NotNull
    @Size(max = 20)
    private String createdBy;

    @NotNull
    private Instant createdDate;

    @Size(max = 20)
    private String lastModifiedBy;

    private Instant lastModifiedDate;



}
