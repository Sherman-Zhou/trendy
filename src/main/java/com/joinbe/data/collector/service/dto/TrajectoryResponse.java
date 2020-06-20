package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

public class TrajectoryResponse {

    @ApiModelProperty(value = "轨迹ID")
    private String trajectoryId;

    @ApiModelProperty(value = "车辆开始的经度")
    private BigDecimal startLongitude;

    @ApiModelProperty(value = "车辆开始的维度")
    private BigDecimal startLatitude;

    @ApiModelProperty(value = "点火时间")
    private Long startDate;

    @ApiModelProperty(value = "熄火时间")
    private Long endDate;

    @ApiModelProperty(value = "轨迹的里程数(KM)")
    private BigDecimal mileage;

    @ApiModelProperty(value = "超速次数")
    private Integer overspeedNum;

    @ApiModelProperty(value = "结算状态")
    private String status;

    private List<TrajectoryResponseItem> dateItem;

    public String getTrajectoryId() {
        return trajectoryId;
    }

    public void setTrajectoryId(String trajectoryId) {
        this.trajectoryId = trajectoryId;
    }

    public BigDecimal getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(BigDecimal startLongitude) {
        this.startLongitude = startLongitude;
    }

    public BigDecimal getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(BigDecimal startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public Integer getOverspeedNum() {
        return overspeedNum;
    }

    public void setOverspeedNum(Integer overspeedNum) {
        this.overspeedNum = overspeedNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TrajectoryResponseItem> getDateItem() {
        return dateItem;
    }

    public void setDateItem(List<TrajectoryResponseItem> dateItem) {
        this.dateItem = dateItem;
    }

    @Override
    public String toString() {
        return "TrajectoryResponse{" +
            "trajectoryId='" + trajectoryId + '\'' +
            ", startLongitude=" + startLongitude +
            ", startLatitude=" + startLatitude +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", mileage=" + mileage +
            ", overspeedNum=" + overspeedNum +
            ", status='" + status + '\'' +
            ", dateItem=" + dateItem +
            '}';
    }
}
