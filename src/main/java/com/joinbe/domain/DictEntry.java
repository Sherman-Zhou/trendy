package com.joinbe.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.joinbe.domain.enumeration.RecordStatus;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 字典数据表
 */
@Entity
@Table(name = "dict_entry")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DictEntry extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典排序
     */
    @Column(name = "sort_order", precision = 21, scale = 2)
    private BigDecimal sortOrder;

    /**
     * 字典标签
     */
    @Size(max = 100)
    @Column(name = "label", length = 100)
    private String label;

    /**
     * 字典键值
     */
    @Size(max = 100)
    @Column(name = "value", length = 100)
    private String value;

    @Column(name = "is_default")
    private Boolean isDefault;

    /**
     * 备注
     */
    @Size(max = 500)
    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "status")
    private RecordStatus status;

    @ManyToOne
    @JsonIgnoreProperties("entries")
    @TableField(exist = false)
    private DictType dictType;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSortOrder() {
        return sortOrder;
    }

    public DictEntry sortOrder(BigDecimal sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public void setSortOrder(BigDecimal sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getLabel() {
        return label;
    }

    public DictEntry label(String label) {
        this.label = label;
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public DictEntry value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isIsDefault() {
        return isDefault;
    }

    public DictEntry isDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getRemark() {
        return remark;
    }

    public DictEntry remark(String remark) {
        this.remark = remark;
        return this;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public DictEntry status(RecordStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }


    public DictType getDictType() {
        return dictType;
    }

    public DictEntry dictType(DictType dictType) {
        this.dictType = dictType;
        return this;
    }

    public void setDictType(DictType dictType) {
        this.dictType = dictType;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DictEntry)) {
            return false;
        }
        return id != null && id.equals(((DictEntry) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "DictEntry{" +
            "id=" + getId() +
            ", sortOrder=" + getSortOrder() +
            ", label='" + getLabel() + "'" +
            ", value='" + getValue() + "'" +
            ", isDefault='" + isIsDefault() + "'" +
            ", remark='" + getRemark() + "'" +
            ", status='" + getStatus() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}
