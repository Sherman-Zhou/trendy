package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class IButtonResponseDTO extends ResponseDTO implements Serializable {

    public IButtonResponseDTO(int code, String message) {
        super(code, message);
    }

    public IButtonResponseDTO(int code, String message, IButtonResponseItem data) {
        super(code, message);
        this.data = data;
    }

    @ApiModelProperty(value = "设备iButton的返回数据")
    private IButtonResponseItem data;

    public IButtonResponseItem getData() {
        return data;
    }

    public void setData(IButtonResponseItem data) {
        this.data = data;
    }
}
