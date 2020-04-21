package com.joinbe.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.joinbe.domain.enumeration.OperationType;
import com.joinbe.domain.enumeration.PermissionType;
import com.joinbe.domain.enumeration.RecordStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permission")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//@Data
//@EqualsAndHashCode(callSuper = false)
public class Permission extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "permission_key", length = 80, nullable = false)
    private String permissionKey;

    @Column(name = "lvl")
    private Integer lvl;

    @Column(name = "permission_type")
    private PermissionType permissionType;

    @Size(max = 50)
    @Column(name = "title_key", length = 50)
    private String titleKey;

    @Size(max = 50)
    @Column(name = "frontend_path", length = 50)
    private String frontendPath;

    @Size(max = 200)
    @Column(name = "component", length = 200)
    private String component;

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

    @Size(max = 50)
    @Column(name = "backend_url", length = 50)
    private String backendUrl;

    @Column(name = "status")
    private RecordStatus status;


    @ManyToOne
    @JsonIgnoreProperties("")
    @TableField(exist = false)
    private Permission parent;

    @OneToMany(mappedBy = "parent")
    @TableField(exist = false)
    private List<Permission> children;


    public Permission addChild(Permission child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        child.setParent(this);
        children.add(child);
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public void setPermissionKey(String key) {
        this.permissionKey = key;
    }

    public Integer getLvl() {
        return lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public void setTitleKey(String title) {
        this.titleKey = title;
    }

    public String getFrontendPath() {
        return frontendPath;
    }

    public void setFrontendPath(String frontendPath) {
        this.frontendPath = frontendPath;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(BigDecimal sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getBackendUrl() {
        return backendUrl;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public void setBackendUrl(String backendUrl) {
        this.backendUrl = backendUrl;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Permission getParent() {
        return parent;
    }

    public void setParent(Permission parent) {
        this.parent = parent;
    }

    public List<Permission> getChildren() {
        return children;
    }

    public void setChildren(List<Permission> children) {
        this.children = children;
    }
}
