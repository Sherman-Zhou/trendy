package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = false)
public class PasswordChangeDTO {

    @ApiModelProperty(value = "用户当前密码", required = true)
    @NotBlank
    private String currentPassword;

    @ApiModelProperty(value = "用户新密码", required = true)
    @NotBlank
    private String newPassword;

    public PasswordChangeDTO() {
        // Empty constructor needed for Jackson.
    }

    public PasswordChangeDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
