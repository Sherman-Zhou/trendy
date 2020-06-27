package com.joinbe.data.collector.service.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class TokenResponseItem implements Serializable {

    /**
     * 设备ID
     */
    @ApiModelProperty(value = "设备IMEI")
    private String imei;

    /**
     * 设备token
     */
    @ApiModelProperty(value = "token")
    private String token;

    /**
     * 设备token的过期时间
     */
    @ApiModelProperty(value = "token过期时间")
    private String expireDate;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
