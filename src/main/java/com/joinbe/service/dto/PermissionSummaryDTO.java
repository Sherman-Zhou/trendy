package com.joinbe.service.dto;

import com.joinbe.domain.Permission;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PermissionSummaryDTO implements Serializable {

    private Long id;

    private String titleKey;

    private String description;

    private Long parentId;

    private List<PermissionSummaryDTO> children;

    private boolean checked;

    private boolean expand;

    public PermissionSummaryDTO(Long id, String titleKey, String description) {
        this.id = id;
        this.titleKey = titleKey;
        this.description = description;
    }

    public PermissionSummaryDTO() {
    }

    public PermissionSummaryDTO(Permission permission) {
        this.id = permission.getId();
        this.description = permission.getDescription();
        this.titleKey= permission.getTitleKey();
        this.parentId = permission.getParent() != null ? permission.getParent().getId() : null;

    }
}
