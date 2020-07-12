package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class MerchantDTO implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "平台名称")
    private String name;

    @ApiModelProperty(value = "平台描述")
    private String description;

    @NotNull
    @Pattern(regexp = "[ADI]")
    @ApiModelProperty(value = "状态", example = "A-已启用，I-已停用， D-已删除", required = true)
    private String status;
}
