package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public class BMacResponseDTO extends ResponseDTO implements Serializable {

    public BMacResponseDTO(int code, String message) {
        super(code, message);
    }

    public BMacResponseDTO(int code, String message, BMacResponseItemDTO data) {
        super(code, message);
        this.data = data;
    }

    private BMacResponseItemDTO data;

    public BMacResponseItemDTO getData() {
        return data;
    }

    public void setData(BMacResponseItemDTO data) {
        this.data = data;
    }
}
