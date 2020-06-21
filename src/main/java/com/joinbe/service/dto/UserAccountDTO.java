package com.joinbe.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserAccountDTO {
    @Size(max = 500)
    @ApiModelProperty(value = "用户地址")
    private String address;

    @Size(max = 50)
    @ApiModelProperty(value = "手机号码")
    private String mobileNo;

    @Size(min = 2, max = 10)
    @ApiModelProperty(value = "用户语言", example = "zh-cn")
    private String langKey;

    @Size(max = 50)
    @ApiModelProperty(value = "用户姓名")
    private String name;

    @Email
    @Size(min = 5, max = 254)
    @NotBlank
    @ApiModelProperty(value = "用户邮件（必须唯一）")
    private String email;

    @Size(max = 256)
    @ApiModelProperty(value = "用户图片地址")
    private String avatar;
}
