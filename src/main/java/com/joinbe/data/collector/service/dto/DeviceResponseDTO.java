package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

public class DeviceResponseDTO extends ResponseDTO implements Serializable {

    public DeviceResponseDTO(int code, String message) {
        super(code, message);
    }

    public DeviceResponseDTO(int code, String message, @Size(max = 100) String identifyNumber, @Size(max = 100) String imei, @Size(max = 50) String version) {
        super(code, message);
        this.identifyNumber = identifyNumber;
        this.imei = imei;
        this.version = version;
    }
    /**
     * 设备ID
     */
    @ApiModelProperty(value = "设备ID")
    private String identifyNumber;

    /**
     * 设备IMEI
     */
    @ApiModelProperty(value = "设备IMEI")
    private String imei;

    /**
     * 固件版本
     */
    @ApiModelProperty(value = "固件版本")
    private String version;

    public String getIdentifyNumber() {
        return identifyNumber;
    }

    public void setIdentifyNumber(String identifyNumber) {
        this.identifyNumber = identifyNumber;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
