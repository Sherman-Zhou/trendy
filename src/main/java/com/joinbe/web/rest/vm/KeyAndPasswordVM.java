package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;

/**
 * View Model object for storing the user's key and password.
 */
public class KeyAndPasswordVM {

    @ApiModelProperty(value = "重置密匙")
    private String key;

    @ApiModelProperty(value = "新密码")
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
