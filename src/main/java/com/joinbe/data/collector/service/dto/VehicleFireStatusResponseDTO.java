package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public class VehicleFireStatusResponseDTO extends ResponseDTO implements Serializable {

    public VehicleFireStatusResponseDTO(int code, String message) {
        super(code, message);
    }

    public VehicleFireStatusResponseDTO(int code, String message, VehicleFireStatusResponseItemDTO data) {
        super(code, message);
        this.data = data;
    }

    private VehicleFireStatusResponseItemDTO data;

    public VehicleFireStatusResponseItemDTO getData() {
        return data;
    }

    public void setData(VehicleFireStatusResponseItemDTO data) {
        this.data = data;
    }
}
