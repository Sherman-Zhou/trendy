package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * View Model object for storing the user's key and password.
 */
public class KeyAndPasswordVM {

    @ApiModelProperty(value = "重置密匙", required = true)
    @NotBlank
    private String key;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank
    private String newPassword;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
