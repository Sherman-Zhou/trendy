package com.joinbe.service.dto;

import com.joinbe.domain.enumeration.OperationType;
import com.joinbe.domain.enumeration.PermissionType;
import com.joinbe.domain.enumeration.RecordStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String name;

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

    private BigDecimal sortOrder;

    @Size(max = 20)
    private String backendUrl;

    private RecordStatus status;

    @NotNull
    @Size(max = 20)
    private String createdBy;

    @NotNull
    private Instant createdDate;

    @Size(max = 20)
    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Long parentId;


}
