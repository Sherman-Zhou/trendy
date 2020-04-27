package com.joinbe.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleDetailsDTO extends RoleDTO {
//    private Integer version;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;


    private Set<PermissionDTO> permissions = new HashSet<>();
}
