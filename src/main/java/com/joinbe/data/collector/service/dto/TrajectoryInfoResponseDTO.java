package com.joinbe.data.collector.service.dto;

import java.io.Serializable;

public class TrajectoryInfoResponseDTO extends ResponseDTO implements Serializable {

    public TrajectoryInfoResponseDTO(int code, String message) {
        super(code, message);
    }

    public TrajectoryInfoResponseDTO(int code, String message, TrajectoryInfoResponseItemDTO data) {
        super(code, message);
        this.data = data;
    }

    private TrajectoryInfoResponseItemDTO data;

    public TrajectoryInfoResponseItemDTO getData() {
        return data;
    }

    public void setData(TrajectoryInfoResponseItemDTO data) {
        this.data = data;
    }
}
