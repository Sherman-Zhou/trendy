package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleDTO implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @ApiModelProperty(value = "角色名", required = true)
    private String name;

    @Size(max = 200)
    @ApiModelProperty(value = "角色描述")
    private String description;

    @Size(max = 20)
    @ApiModelProperty(value = "角色代码")
    private String code;

    @NotNull
    @Pattern(regexp = "[ADI]")
    @ApiModelProperty(value = "状态", example = "A-已启用，I-已停用， D-已删除",  required = true)
    private String status;

}
