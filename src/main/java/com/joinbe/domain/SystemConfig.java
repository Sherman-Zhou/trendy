package com.joinbe.domain;


import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * A SystemConfig.
 */
@Entity
@Table(name = "sys_config")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SystemConfig extends AbstractAuditingEntity {

    public static final String TRAJECTORY_RESERVE_DAYS = "TRAJECTORY_RESERVE_DAYS";

    public static final String LAST_BACKUP_TIME = "LAST_BACKUP_TIME";

    public static final String MILEAGE_MULTIPLE = "MILEAGE_MULTIPLE";

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 参数key值
     */
    @Size(max = 50)
    @Column(name = "config_key", length = 50)
    private String key;

    /**
     * 参数value值
     */
    @Size(max = 50)
    @Column(name = "config_value", length = 50)
    private String value;

    @Size(max = 1)
    @Column(name = "status", length = 1)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Merchant merchant;


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

    public SystemConfig key(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SystemConfig value(String value) {
        this.value = value;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SystemConfig status(String status) {
        this.status = status;
        return this;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    @Override
    public String toString() {
        return "SystemConfig{" +
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
