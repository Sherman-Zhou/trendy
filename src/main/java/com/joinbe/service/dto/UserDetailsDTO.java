package com.joinbe.service.dto;

import com.joinbe.domain.Role;
import com.joinbe.domain.User;
import com.joinbe.domain.enumeration.RecordStatus;
import com.joinbe.service.DivisionService;
import com.joinbe.service.RoleService;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDetailsDTO extends UserDTO {

    @ApiModelProperty(value = "用户角色名",hidden = true)
    private Set<String> authorities;

    @ApiModelProperty(value = "用户角色")
    private Set<RoleDTO> roles;

    @ApiModelProperty(value = "用户部门")
    private Set<DivisionDTO> divisions;

    @ApiModelProperty(value = "版本")
    private Integer version;

    @ApiModelProperty(value = "创建者")
    private String createdBy;

    @ApiModelProperty(value = "创建时间")
    private Instant createdDate;

    @ApiModelProperty(value = "最好更新者")
    private String lastModifiedBy;

    @ApiModelProperty(value = "最后更新时间")
    private Instant lastModifiedDate;

    @ApiModelProperty(value = "用户权限菜单")
    private List<PermissionDTO> menus;

    @ApiModelProperty(value = "用户权限")
    private List<PermissionDTO> permissions;

    public UserDetailsDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDetailsDTO(User user) {
        this.setId(user.getId());
        this.setLogin(user.getLogin());
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setStatus(user.getStatus() != null ? user.getStatus().getCode() : null);
        this.setAvatar(user.getAvatar());
        this.setLangKey(user.getLangKey());
        this.version = user.getVersion();
        this.roles = user.getRoles().stream()
            .map(RoleService::toDto)
            .collect(Collectors.toSet());
        this.authorities = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
        this.divisions = user.getDivisions().stream()
            .map(DivisionService::toDto)
            .collect(Collectors.toSet());
    }

    public Boolean getActivated() {
        return RecordStatus.ACTIVE.getCode().equals(this.getStatus());
    }

}
