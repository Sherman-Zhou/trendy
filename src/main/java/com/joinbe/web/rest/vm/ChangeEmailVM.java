package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class ChangeEmailVM {
    @ApiModelProperty(value = "用户登陆id")
    @NotNull
    private String login;

    @ApiModelProperty(value = "用户邮箱")
    @NotNull
    @Email
    private String email;

    @ApiModelProperty(value = "用户密码")
    @NotNull
    private String password;
}
