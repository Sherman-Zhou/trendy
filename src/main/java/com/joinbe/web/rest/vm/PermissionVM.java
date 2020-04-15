package com.joinbe.web.rest.vm;

import com.joinbe.domain.enumeration.OperationType;
import com.joinbe.domain.enumeration.PermissionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionVM implements Serializable {

    private String key;

    private Integer lvl;

    private PermissionType permissionType;

    @Size(max = 20)
    private String title;

    @Size(max = 20)
    private String frontendPath;

    @Size(max = 20)
    private String icon;

    private OperationType operationType;

    @Size(max = 200)
    private String description;
}
