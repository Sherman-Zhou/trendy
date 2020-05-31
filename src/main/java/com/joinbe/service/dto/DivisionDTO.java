package com.joinbe.service.dto;

import com.joinbe.domain.enumeration.RecordStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class DivisionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    @ApiModelProperty(value = "部门名", required = true)
    private String name;

    @Size(max = 500)
    @ApiModelProperty(value = "部门描述" )
    private String description;

    @Size(max = 20)
    @ApiModelProperty(value = "部门代码")
    private String code;

    @ApiModelProperty(value = "状态")
    private RecordStatus status;

    @ApiModelProperty(value = "上级部门主键")
    private Long parentId;

    @ApiModelProperty(value = "是否有子部门")
    private boolean hasChildren;

    @ApiModelProperty(value = "子部门")
    private List<DivisionDTO> children;



}
