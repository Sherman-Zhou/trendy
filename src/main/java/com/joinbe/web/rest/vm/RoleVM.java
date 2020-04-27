package com.joinbe.web.rest.vm;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleVM implements Serializable {

    private String name;


    private String description;


    private String code;

    private String status;

}
