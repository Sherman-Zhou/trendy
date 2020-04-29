package com.joinbe.domain;


import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * A EquipmentConfig.
 */
@Entity
@Table(name = "equipment_config")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EquipmentConfig extends AbstractAuditingEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 参数key值
     */
    @Size(max = 20)
    @Column(name = "jhi_key", length = 20)
    private String key;

    /**
     * 参数value值
     */
    @Size(max = 50)
    @Column(name = "value", length = 50)
    private String value;

    @Size(max = 1)
    @Column(name = "status", length = 1)
    private String status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public EquipmentConfig key(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public EquipmentConfig value(String value) {
        this.value = value;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EquipmentConfig status(String status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "EquipmentConfig{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", value='" + getValue() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
