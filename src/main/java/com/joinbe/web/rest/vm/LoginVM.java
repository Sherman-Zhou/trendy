package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * View Model object for storing a user's credentials.
 */
//@ApiModel(value = "用户登陆对象", description = "用户登陆对象")
public class LoginVM {

    @NotNull
    @Size(min = 1, max = 50)
    @ApiModelProperty(value = "用户名或者邮箱地址", required = true)

    private String username;

    @NotNull
    @Size(min = 4, max = 100)
    @ApiModelProperty(value = "用户密码", required= true)
    private String password;

    @ApiModelProperty(value = "是否记住用户", hidden =true)
    private Boolean rememberMe;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Override
    public String toString() {
        return "LoginVM{" +
            "username='" + username + '\'' +
            ", rememberMe=" + rememberMe +
            '}';
    }
}
