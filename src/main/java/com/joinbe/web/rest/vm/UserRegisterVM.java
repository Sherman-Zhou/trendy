package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserRegisterVM {
    @ApiModelProperty(value = "用户登陆id")
    private String login;

    @ApiModelProperty(value = "用户邮箱")
    private String email;

    @ApiModelProperty(value = "用户密码")
    private String password;
}