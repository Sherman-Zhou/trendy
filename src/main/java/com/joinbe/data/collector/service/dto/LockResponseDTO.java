package com.joinbe.data.collector.service.dto;

import com.joinbe.data.collector.netty.protocol.message.LockUnlockProtocol;

import java.io.Serializable;

public class LockResponseDTO extends ResponseDTO implements Serializable {

    public LockResponseDTO(int code, String message) {
        super(code, message);
    }

    public LockResponseDTO(int code, String message, LockUnlockProtocol data) {
        super(code, message);
        this.data = data;
    }

    private LockUnlockProtocol data;

    public LockUnlockProtocol getData() {
        return data;
    }

    public void setData(LockUnlockProtocol data) {
        this.data = data;
    }
}
