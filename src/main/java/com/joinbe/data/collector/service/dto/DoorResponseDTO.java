package com.joinbe.data.collector.service.dto;

import com.joinbe.data.collector.netty.protocol.message.DoorProtocol;

import java.io.Serializable;

public class DoorResponseDTO extends ResponseDTO implements Serializable {

    public DoorResponseDTO(int code, String message) {
        super(code, message);
    }

    public DoorResponseDTO(int code, String message, DoorProtocol data) {
        super(code, message);
        this.data = data;
    }

    private DoorProtocol data;

    public DoorProtocol getData() {
        return data;
    }

    public void setData(DoorProtocol data) {
        this.data = data;
    }
}
