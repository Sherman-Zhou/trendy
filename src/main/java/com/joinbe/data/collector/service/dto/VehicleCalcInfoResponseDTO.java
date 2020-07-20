package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public class VehicleCalcInfoResponseDTO extends ResponseDTO implements Serializable {

    public VehicleCalcInfoResponseDTO(int code, String message) {
        super(code, message);
    }

    public VehicleCalcInfoResponseDTO(int code, String message, VehicleCalcInfoResponseItemDTO data) {
        super(code, message);
        this.data = data;
    }

    private VehicleCalcInfoResponseItemDTO data;

    public VehicleCalcInfoResponseItemDTO getData() {
        return data;
    }

    public void setData(VehicleCalcInfoResponseItemDTO data) {
        this.data = data;
    }
}
