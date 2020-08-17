package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public class DLFWResponseDTO extends ResponseDTO implements Serializable {

    public DLFWResponseDTO(int code, String message) {
        super(code, message);
    }

    public DLFWResponseDTO(int code, String message, DLFWeResponseItemDTO data) {
        super(code, message);
        this.data = data;
    }

    private DLFWeResponseItemDTO data;

    public DLFWeResponseItemDTO getData() {
        return data;
    }

    public void setData(DLFWeResponseItemDTO data) {
        this.data = data;
    }
}
