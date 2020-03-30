package com.joinbe.service.dto;

import com.joinbe.config.Constants;
import com.joinbe.domain.Role;
import com.joinbe.domain.User;
import com.joinbe.domain.enumeration.RecordStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO {

    private Long id;

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String name;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(max = 256)
    private String avatar;

    @Size(max = 2000)
    private String remark;

    @Size(max = 500)
    private String address;

    @Size(min = 2, max = 10)
    private String langKey;

    private RecordStatus status;

    private Set<String> authorities;

    private Set<RoleDTO> roles;

    private List<PermissionDTO> permissions;

    public UserDTO() {
        // Empty constructor needed for Jackson.
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.name = user.getName();
        this.email = user.getEmail();
        this.status = user.getStatus();
        this.avatar = user.getAvatar();
        this.langKey = user.getLangKey();
        this.authorities = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
    }

    public Boolean getActivated() {
        return RecordStatus.ACTIVE.equals(status);
    }


}
