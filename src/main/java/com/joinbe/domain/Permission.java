package com.joinbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.joinbe.domain.enumeration.OperationType;
import com.joinbe.domain.enumeration.PermissionType;
import com.joinbe.domain.enumeration.RecordStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permission")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Permission extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "name", length = 80, nullable = false)
    private String name;

    @Column(name = "lvl")
    private Integer lvl;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type")
    private PermissionType permissionType;

    @Size(max = 20)
    @Column(name = "title", length = 20)
    private String title;

    @Size(max = 20)
    @Column(name = "frontend_path", length = 20)
    private String frontendPath;

    @Size(max = 20)
    @Column(name = "icon", length = 20)
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    private OperationType operationType;

    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "sort_order", precision = 21, scale = 2)
    private BigDecimal sortOrder;

    @Size(max = 20)
    @Column(name = "backend_url", length = 20)
    private String backendUrl;

    @Column(name = "status")
    private RecordStatus status;


    @ManyToOne
    @JsonIgnoreProperties("")
    private Permission parent;

    @OneToMany(mappedBy = "parent")
    private List<Permission> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Permission name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLvl() {
        return lvl;
    }

    public Permission lvl(Integer lvl) {
        this.lvl = lvl;
        return this;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public Permission permissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
        return this;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }

    public String getTitle() {
        return title;
    }

    public Permission title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFrontendPath() {
        return frontendPath;
    }

    public Permission frontendPath(String frontendPath) {
        this.frontendPath = frontendPath;
        return this;
    }

    public void setFrontendPath(String frontendPath) {
        this.frontendPath = frontendPath;
    }

    public String getIcon() {
        return icon;
    }

    public Permission icon(String icon) {
        this.icon = icon;
        return this;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public Permission operationType(OperationType operationType) {
        this.operationType = operationType;
        return this;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getDescription() {
        return description;
    }

    public Permission description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSortOrder() {
        return sortOrder;
    }

    public Permission sortOrder(BigDecimal sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public void setSortOrder(BigDecimal sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getBackendUrl() {
        return backendUrl;
    }

    public Permission backendUrl(String backendUrl) {
        this.backendUrl = backendUrl;
        return this;
    }

    public void setBackendUrl(String backendUrl) {
        this.backendUrl = backendUrl;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public Permission status(RecordStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Permission getParent() {
        return parent;
    }

    public Permission parent(Permission permission) {
        this.parent = permission;
        return this;
    }

    public void setParent(Permission permission) {
        this.parent = permission;
    }

    public List<Permission> getChildren() {
        return children;
    }

    public void setChildren(List<Permission> children) {
        this.children = children;
    }

    public Permission addChild(Permission child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        child.setParent(this);
        children.add(child);
        return this;
    }

    @Override
    public String toString() {
        return "Permission{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", lvl=" + getLvl() +
            ", permissionType='" + getPermissionType() + "'" +
            ", title='" + getTitle() + "'" +
            ", frontendPath='" + getFrontendPath() + "'" +
            ", icon='" + getIcon() + "'" +
            ", operationType='" + getOperationType() + "'" +
            ", description='" + getDescription() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", backendUrl='" + getBackendUrl() + "'" +
            ", recordStatus='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
