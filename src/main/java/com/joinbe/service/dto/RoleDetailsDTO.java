package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleDetailsDTO extends RoleDTO {
//    private Integer version;

    @ApiModelProperty(value = "创建者")
    private String createdBy;

    @ApiModelProperty(value = "创建时间")
    private Instant createdDate;

    @ApiModelProperty(value = "最后更新者")
    private String lastModifiedBy;

    @ApiModelProperty(value = "最后更新时间")
    private Instant lastModifiedDate;

//    @ApiModelProperty(value = "用户菜单权限")
//    private Set<PermissionDTO> permissions = new HashSet<>();
}
