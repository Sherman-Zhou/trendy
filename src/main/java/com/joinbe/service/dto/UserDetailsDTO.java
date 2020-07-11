package com.joinbe.service.dto;

import com.joinbe.domain.Role;
import com.joinbe.domain.Staff;
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

    public UserDetailsDTO(Staff staff) {
        this.setId(staff.getId());
        this.setLogin(staff.getLogin());
        this.setName(staff.getName());
        this.setEmail(staff.getEmail());
        this.setStatus(staff.getStatus() != null ? staff.getStatus().getCode() : null);
        this.setAvatar(staff.getAvatar());
        this.setLangKey(staff.getLangKey());
        this.version = staff.getVersion();
        this.roles = staff.getRoles().stream()
            .map(RoleService::toDto)
            .collect(Collectors.toSet());
        this.authorities = staff.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
        this.divisions = staff.getDivisions().stream()
            .map(DivisionService::toDto)
            .collect(Collectors.toSet());
    }

    public Boolean getActivated() {
        return RecordStatus.ACTIVE.getCode().equals(this.getStatus());
    }

}
