package com.joinbe.service.dto;

import com.joinbe.config.Constants;
import com.joinbe.domain.Role;
import com.joinbe.domain.User;
import com.joinbe.domain.enumeration.Sex;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO implements Serializable {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

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

    private String sex;

    @Size(max = 500)
    private String address;

    @Size(min = 2, max = 10)
    private String langKey;

    private String status;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    private String roleName;

    private List<Long> roleIds;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.setId(user.getId());
        this.setLogin(user.getLogin());
        this.setName(user.getName());
        this.setEmail(user.getEmail());
        this.setStatus(user.getStatus() != null ? user.getStatus().getCode() : null);
        this.setAvatar(user.getAvatar());
        this.setSex(Sex.getCode(user.getSex()));
        this.setLangKey(user.getLangKey());
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
            this.setRoleName(String.join(", ", roleNames));
        }

    }


}
