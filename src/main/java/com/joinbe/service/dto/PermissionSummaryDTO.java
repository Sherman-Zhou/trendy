package com.joinbe.service.dto;

import com.joinbe.domain.Permission;
import com.joinbe.domain.enumeration.PermissionType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionSummaryDTO implements Serializable {

    @ApiModelProperty(value = "id", hidden = true)
    private Long id;

    @ApiModelProperty(value = "权限i18键")
    private String titleKey;

    @ApiModelProperty(value = "权限描述")
    private String description;

    @ApiModelProperty(value = "父权限主键")
    private Long parentId;

    @ApiModelProperty(value = "权限类型: FOLDER-目录， MENU-菜单， OPERATION-操作 ")
    private PermissionType permissionType;

    @ApiModelProperty(value = "子权限列表")
    private List<PermissionSummaryDTO> children;

    @ApiModelProperty(value = "当前角色是否选中该权限")
    private boolean checked;

    @ApiModelProperty(value = "是否展开")
    private boolean expand;

    public PermissionSummaryDTO(Long id, String titleKey, String description) {
        this.id = id;
        this.titleKey = titleKey;
        this.description = description;
    }

    public PermissionSummaryDTO() {
    }

    public PermissionSummaryDTO(Permission permission) {
        this.id = permission.getId();
        this.description = permission.getDescription();
        this.titleKey = permission.getTitleKey();
        this.permissionType = permission.getPermissionType();
        this.parentId = permission.getParent() != null ? permission.getParent().getId() : null;

    }
}
