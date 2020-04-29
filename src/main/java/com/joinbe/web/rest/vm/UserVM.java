package com.joinbe.web.rest.vm;

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
//    private String login;

    @Size(max = 50)
    private String name;

    @Email
    @Size(min = 5, max = 254)
    private String email;
//
//    @Size(max = 256)
//    private String avatar;

//    @Size(max = 2000)
//    private String remark;

    @Size(max = 500)
    private String address;

    @Size(min = 2, max = 10)
    private String langKey;

    private String status;

    @Override
    public String toString() {
        return "ManagedUserVM{" + super.toString() + "} ";
    }
}
