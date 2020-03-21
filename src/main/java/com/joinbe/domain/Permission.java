package com.joinbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.joinbe.domain.enumeration.OperationType;
import com.joinbe.domain.enumeration.PermissionType;
import com.joinbe.domain.enumeration.RecordStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@Data
@EqualsAndHashCode(callSuper = false)
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


    public Permission addChild(Permission child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        child.setParent(this);
        children.add(child);
        return this;
    }
}
