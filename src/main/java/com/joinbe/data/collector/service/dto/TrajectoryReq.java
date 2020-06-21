package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.util.Date;

public class TrajectoryReq {

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
    @NotEmpty(message = "start date cant not be empty")
    @Past(message = "start date must be past date")
    private Date startDateFrom;

    /**
     * 结束日期From - 时间戳
     */
    @ApiModelProperty(value = "结束时间，时间戳格式，结束时间为空则默认为当前系统时间")
    @NotEmpty(message = "end date cant not be empty")
    private Date endDateFrom;

    /**
     * 页序号
     */
    @ApiModelProperty(value = "页序号")
    @NotNull(message = "页序号不能为空")
    @Min(value = 0, message = "页序号要大于0")
    private Integer pageNum;
    /**
     * 每页显示记录数
     */
    @ApiModelProperty(value = "每页显示记录数")
    @NotNull(message = "每页显示记录数不能为空")
    @Min(value = 1, message = "每页显示记录数要大于1")
    private Integer pageSize;

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Date getStartDateFrom() {
        return startDateFrom;
    }

    public void setStartDateFrom(Date startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public Date getEndDateFrom() {
        return endDateFrom;
    }

    public void setEndDateFrom(Date endDateFrom) {
        this.endDateFrom = endDateFrom;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "TrajectoryReq{" +
            "plateNumber='" + plateNumber + '\'' +
            ", startDateFrom=" + startDateFrom +
            ", endDateFrom=" + endDateFrom +
            ", pageNum=" + pageNum +
            ", pageSize=" + pageSize +
            '}';
    }
}
