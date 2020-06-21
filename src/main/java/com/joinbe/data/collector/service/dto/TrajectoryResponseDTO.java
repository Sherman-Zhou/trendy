package com.joinbe.data.collector.service.dto;

import java.io.Serializable;
import java.util.List;

public class TrajectoryResponseDTO extends ResponseForPageDTO implements Serializable {

    private List<TrajectoryResponseResult> data;

    public TrajectoryResponseDTO(int code, String message) {
        super(code, message);
    }

    public TrajectoryResponseDTO(int code, String message, Long totalCount, Integer pageNum, Integer pageSize) {
        super(code, message, totalCount, pageNum, pageSize);
    }

    public TrajectoryResponseDTO(int code, String message, List<TrajectoryResponseResult> data) {
        super(code, message);
        this.data = data;
    }

    public TrajectoryResponseDTO(int code, String message, Long totalCount, Integer pageNum, Integer pageSize, List<TrajectoryResponseResult> data) {
        super(code, message, totalCount, pageNum, pageSize);
        this.data = data;
    }

    public List<TrajectoryResponseResult> getData() {
        return data;
    }

    public void setData(List<TrajectoryResponseResult> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TrajectoryResponseDTO{" +
            "data=" + data +
            '}';
    }
}
