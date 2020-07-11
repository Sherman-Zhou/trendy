package com.joinbe.service.dto;

import com.joinbe.config.Constants;
import com.joinbe.domain.Role;
import com.joinbe.domain.Staff;
import com.joinbe.domain.enumeration.Sex;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.*;
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
    @ApiModelProperty(value = "用户登陆id（必须唯一）")
    private String login;

    @Size(max = 50)
    @ApiModelProperty(value = "用户姓名")
    private String name;

    @Email
    @Size(min = 5, max = 254)
    @ApiModelProperty(value = "用户邮件（必须唯一）")
    private String email;

    @Size(max = 256)
    @ApiModelProperty(value = "用户图片地址")
    private String avatar;

    @Size(max = 2000)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "性别", example = "M -男， F-女， U-未知")
    @Size( max = 1)
    private String sex;

    @Size(max = 500)
    @ApiModelProperty(value = "用户地址")
    private String address;

    @Size(max = 50)
    @ApiModelProperty(value = "手机号码")
    private String mobileNo;

    @Size(min = 2, max = 10)
    @ApiModelProperty(value = "用户语言", example = "zh-cn")
    private String langKey;

    @ApiModelProperty(value = "用户状态", example = "A-已启用，I-已停用， D-已删除")
    private String status;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    //@NotBlank
    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "用户角色名（,分隔）")
    private String roleName;

    @ApiModelProperty(value = "用户角色id列表")
    @NotEmpty
    private List<Long> roleIds;

    @ApiModelProperty("部门id列表")
    private List<String> divisionIds;

    public UserDTO() {
    }

    public UserDTO(Staff staff) {
        this.setId(staff.getId());
        this.setLogin(staff.getLogin());
        this.setName(staff.getName());
        this.setEmail(staff.getEmail());
        this.setMobileNo(staff.getMobileNo());
        this.setStatus(staff.getStatus() != null ? staff.getStatus().getCode() : null);
        this.setAvatar(staff.getAvatar());
        this.setSex(Sex.getCode(staff.getSex()));
        this.setLangKey(staff.getLangKey());
        this.setAddress(staff.getAddress());
        this.setRemark(staff.getRemark());
        if (!CollectionUtils.isEmpty(staff.getRoles())) {
            Set<String> roleNames = staff.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
            this.setRoleName(String.join(", ", roleNames));
        }

    }


}
