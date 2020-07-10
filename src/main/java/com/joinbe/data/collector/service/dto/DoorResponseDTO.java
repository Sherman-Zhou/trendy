package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public class DoorResponseDTO extends ResponseDTO implements Serializable {

    public DoorResponseDTO(int code, String message) {
        super(code, message);
    }

    public DoorResponseDTO(int code, String message, DoorResponseItemDTO data) {
        super(code, message);
        this.data = data;
    }

    private DoorResponseItemDTO data;

    public DoorResponseItemDTO getData() {
        return data;
    }

    public void setData(DoorResponseItemDTO data) {
        this.data = data;
    }
}
