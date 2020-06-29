package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class UploadResponse implements Serializable {

    @ApiModelProperty("总行数")
    private long totalRowsNum = 0;
    @ApiModelProperty("出错行数")
    private long failedRowNum = 0;
    @ApiModelProperty("成功行数")
    private long successRowNum = 0;

    @ApiModelProperty(value = "错误原因列表", notes = "一行有多个错误时会有多条出错原因")
    private List<RowParseError> errors = new ArrayList<>();

    public void increaseFailedRowNum() {
        this.failedRowNum++;
    }

    public void increaseSuccessRowNum() {
        this.successRowNum++;
    }

    public void increaseTotalRowsNum() {
        this.totalRowsNum++;
    }

    public void addError(RowParseError error) {
        this.errors.add(error);
    }

    public void successToError() {
        this.failedRowNum++;
        this.successRowNum--;
    }
}
