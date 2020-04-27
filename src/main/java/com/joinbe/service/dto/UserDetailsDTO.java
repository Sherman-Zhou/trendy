package com.joinbe.service.dto;

import com.joinbe.domain.Role;
import com.joinbe.domain.User;
import com.joinbe.domain.enumeration.RecordStatus;
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

    private Set<String> authorities;

    private Set<RoleDTO> roles;

    private Integer version;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private List<PermissionDTO> menus;

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
        this.authorities = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
    }

    public Boolean getActivated() {
        return RecordStatus.ACTIVE.equals(this.getStatus());
    }

}
