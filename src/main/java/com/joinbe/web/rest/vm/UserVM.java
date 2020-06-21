package com.joinbe.web.rest.vm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserVM implements Serializable {

    public UserVM() {
        // Empty constructor needed for Jackson.
    }

//    @NotBlank
//    @Pattern(regexp = Constants.LOGIN_REGEX)
//    @Size(min = 1, max = 50)
//    @ApiModelProperty(value = "用户登陆id")
//    private String login;

    @Size(max = 50)
    @ApiModelProperty(value = "用户姓名")
    private String name;

    @Email
    @Size(min = 5, max = 254)
    @ApiModelProperty(value = "用户邮件")
    private String email;

    @ApiModelProperty(value = "手机号码")
    private String mobileNo;
//
//    @Size(max = 256)
//    private String avatar;

//    @Size(max = 2000)
//    private String remark;

    @Size(max = 500)
    @ApiModelProperty(value = "用户地址")
    private String address;

    @Size(min = 2, max = 10)
    @ApiModelProperty(value = "用户语言", example = "zh-cn")
    private String langKey;

    @ApiModelProperty(value = "用户状态", example = "A-已启用，I-已停用， D-已删除")
    private String status;

}
