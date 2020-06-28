package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RowParseError implements Serializable {

    @ApiModelProperty(value = "行号", notes = "标题列排除在外")
    private Long rowNum;
    @ApiModelProperty("错误原因")
    private String msg;
    //private Boolean isSuccess;
}
