package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

public class TrajectoryInfoReq {

    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号")
    @NotEmpty(message = "plate number cant not be empty")
    @Length(min = 1, max = 10, message = "plate number's length should between 1~10")
    private String plateNumber;

    /**
     * 开始日期From - 时间戳
     */
    @ApiModelProperty(value = "开始时间，时间戳")
    @NotNull(message = "start date cant not be empty")
    @Past(message = "start date must be past date")
    private Date startDate;

    /**
     * 结束日期From - 时间戳
     */
    @ApiModelProperty(value = "结束时间，时间戳格式")
    @NotNull(message = "end date cant not be empty")
    private Date endDate;


    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    @Override
    public String toString() {
        return "TrajectoryInfoReq{" +
            "plateNumber='" + plateNumber + '\'' +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            '}';
    }
}
