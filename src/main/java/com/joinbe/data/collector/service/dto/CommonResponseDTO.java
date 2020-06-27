package com.joinbe.data.collector.service.dto;

import com.joinbe.data.collector.netty.protocol.message.CommonProtocol;

import java.io.Serializable;

public class CommonResponseDTO extends ResponseDTO implements Serializable {

    public CommonResponseDTO(int code, String message) {
        super(code, message);
    }

    public CommonResponseDTO(int code, String message, CommonProtocol data) {
        super(code, message);
        this.data = data;
    }

    private CommonProtocol data;

    public CommonProtocol getData() {
        return data;
    }

    public void setData(CommonProtocol data) {
        this.data = data;
    }
}
