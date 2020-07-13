package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public class BleResponseDTO extends ResponseDTO implements Serializable {

    public BleResponseDTO(int code, String message) {
        super(code, message);
    }

    public BleResponseDTO(int code, String message, BleResponseItemDTO data) {
        super(code, message);
        this.data = data;
    }

    private BleResponseItemDTO data;

    public BleResponseItemDTO getData() {
        return data;
    }

    public void setData(BleResponseItemDTO data) {
        this.data = data;
    }
}
