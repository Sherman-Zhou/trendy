package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRegisterVM {
    @ApiModelProperty(value = "用户登陆id")
    private String login;

    @ApiModelProperty(value = "用户邮箱")
    private String email;
}
