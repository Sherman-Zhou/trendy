package com.joinbe.data.collector.service.dto;

import com.joinbe.data.collector.netty.protocol.message.PositionProtocol;

import java.io.Serializable;

public class LocationResponseDTO extends ResponseDTO implements Serializable {

    public LocationResponseDTO(int code, String message) {
        super(code, message);
    }

    public LocationResponseDTO(int code, String message, PositionProtocol data) {
        super(code, message);
        this.data = data;
    }

    private PositionProtocol data;

    public PositionProtocol getData() {
        return data;
    }

    public void setData(PositionProtocol data) {
        this.data = data;
    }
}
