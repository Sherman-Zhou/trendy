package com.joinbe.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.joinbe.domain.DictEntry} entity.
 */
@ApiModel(description = "字典数据表")
public class DictEntryDTO implements Serializable {

    private Long id;

    /**
     * 字典排序
     */
    @ApiModelProperty(value = "字典排序")
    private BigDecimal sortOrder;

    /**
     * 字典标签
     */
    @Size(max = 100)
    @ApiModelProperty(value = "字典标签")
    private String label;

    /**
     * 字典键值
     */
    @Size(max = 100)
    @ApiModelProperty(value = "字典键值")
    private String value;

    private Boolean isDefault;

    /**
     * 备注
     */
    @Size(max = 500)
    @ApiModelProperty(value = "备注")
    private String remark;

    @NotNull
    @Size(max = 1)
    private String status;

    @Size(max = 50)
    private String lastModifiedBy;

    private Instant lastModifiedDate;

    @NotNull
    @Size(max = 50)
    private String createdBy;

    private Instant createdDate;


    private Long dictTypeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(BigDecimal sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Long getDictTypeId() {
        return dictTypeId;
    }

    public void setDictTypeId(Long dictTypeId) {
        this.dictTypeId = dictTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DictEntryDTO dictEntryDTO = (DictEntryDTO) o;
        if (dictEntryDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), dictEntryDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DictEntryDTO{" +
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
            ", dictTypeId=" + getDictTypeId() +
            "}";
    }
}
