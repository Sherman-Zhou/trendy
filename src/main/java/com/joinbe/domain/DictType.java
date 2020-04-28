package com.joinbe.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.joinbe.domain.enumeration.RecordStatus;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * 字典类型表
 */
@Entity
@Table(name = "dict_type")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DictType extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典名称
     */
    @Size(max = 100)
    @Column(name = "dic_name", length = 100)
    private String name;

    /**
     * 字典类型
     */
    @Size(max = 100)
    @Column(name = "dic_type", length = 100)
    private String type;

    /**
     * 备注
     */
    @Size(max = 500)
    @Column(name = "remark", length = 500)
    private String remark;


    @Column(name = "status")
    private RecordStatus status;

    @OneToMany(mappedBy = "dictType")
//    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @TableField(exist = false)
    private Set<DictEntry> entries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public DictType name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public DictType type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public DictType remark(String remark) {
        this.remark = remark;
        return this;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public DictType status(RecordStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public Set<DictEntry> getEntries() {
        return entries;
    }

    public DictType entries(Set<DictEntry> dictEntries) {
        this.entries = dictEntries;
        return this;
    }

    public DictType addEntries(DictEntry dictEntry) {
        this.entries.add(dictEntry);
        dictEntry.setDictType(this);
        return this;
    }

    public DictType removeEntries(DictEntry dictEntry) {
        this.entries.remove(dictEntry);
        dictEntry.setDictType(null);
        return this;
    }

    public void setEntries(Set<DictEntry> dictEntries) {
        this.entries = dictEntries;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DictType)) {
            return false;
        }
        return id != null && id.equals(((DictType) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "DictType{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", remark='" + getRemark() + "'" +
            ", status='" + getStatus() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}
