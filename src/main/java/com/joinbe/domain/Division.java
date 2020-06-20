package com.joinbe.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.joinbe.domain.enumeration.RecordStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * A Department.
 */
@Entity
@Table(name = "division")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)

public class Division extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 20)
    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "status")
    private RecordStatus status;

    @Column(name = "parent_id", updatable = false, insertable = false)
    private Long parentId;

    public Division() {
    }
    public Division(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JsonIgnoreProperties("divisions")
    @TableField(exist = false)
    private Division parent;

    @OneToMany(mappedBy = "parent")
    @TableField(exist = false)
    private List<Division> children = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Division getParent() {
        return parent;
    }

    public void setParent(Division parent) {
        this.parent = parent;
    }

    public List<Division> getChildren() {
        return children;
    }

    public void setChildren(List<Division> children) {
        this.children = children;
    }
}
