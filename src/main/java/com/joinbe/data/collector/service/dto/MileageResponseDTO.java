package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public class MileageResponseDTO extends ResponseDTO implements Serializable {

    public MileageResponseDTO(int code, String message) {
        super(code, message);
    }

    public MileageResponseDTO(int code, String message, MileageResponseItemDTO data) {
        super(code, message);
        this.data = data;
    }

    private MileageResponseItemDTO data;

    public MileageResponseItemDTO getData() {
        return data;
    }

    public void setData(MileageResponseItemDTO data) {
        this.data = data;
    }
}
