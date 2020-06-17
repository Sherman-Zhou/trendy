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

    public DeviceResponseDTO(int code, String message, DeviceResponseItem data) {
        super(code, message);
        this.data = data;
    }

    private DeviceResponseItem data;

    public DeviceResponseItem getData() {
        return data;
    }

    public void setData(DeviceResponseItem data) {
        this.data = data;
    }
}
